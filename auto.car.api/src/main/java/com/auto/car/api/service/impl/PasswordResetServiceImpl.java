package com.auto.car.api.service.impl;

import com.auto.car.api.dto.request.PasswordResetConfirmRequest;
import com.auto.car.api.dto.request.PasswordResetRequest;
import com.auto.car.api.dto.request.PasswordResetValidateRequest;
import com.auto.car.api.enums.ContactType;
import com.auto.car.api.enums.error.ErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.repository.ParameterRepository;
import com.auto.car.api.repository.PasswordHistoryRepository;
import com.auto.car.api.repository.PasswordResetTokenRepository;
import com.auto.car.api.repository.UserRepository;
import com.auto.car.api.repository.entity.ContactEntity;
import com.auto.car.api.repository.entity.PasswordHistoryEntity;
import com.auto.car.api.repository.entity.PasswordResetTokenEntity;
import com.auto.car.api.repository.entity.UserEntity;
import com.auto.car.api.service.PasswordResetService;
import com.auto.car.api.service.SendMailService;
import com.auto.car.api.service.dto.SendMailRequest;
import com.auto.car.api.util.PasswordUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final int MAX_REQUESTS_BEFORE_BLOCK = 3; // 3 tentativas permitidas, bloqueio na 4ª
    private static final int BLOCK_DURATION_MINUTES = 30;
    private static final int PASSWORD_HISTORY_SIZE = 5; // Últimas 5 senhas
    private static final String PARAM_KEY_RESET_EMAIL = "email-password-reset-html";
    private static final String PARAM_KEY_PASSWORD_CHANGED_EMAIL = "email-password-changed-html";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private SendMailService sendMailService;

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        String username = request.getUsername();

        // Busca o usuário
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Tentativa de reset de senha para usuário inexistente: {}", username);
                    return createException(ErrorEnum.ERROR_RESET_USER_NOT_FOUND);
                });

        // Verifica bloqueio temporário (3 solicitações em 30 min = bloqueio de 30 min)
        checkTemporaryBlock(username);

        // Gera código de 6 dígitos
        String code = generateCode();

        // Cria o token de reset
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setId(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));
        tokenRepository.save(token);

        // Obtém o e-mail do usuário
        String userEmail = getUserEmail(user);

        // Envia o e-mail com o código
        sendResetCodeEmail(user, userEmail, code);

        log.info("Código de reset de senha enviado para usuário: {}", username);
    }

    /**
     * Verifica se o usuário está bloqueado temporariamente.
     * Regra: Se já solicitou 3 códigos em menos de 30 minutos, bloqueia novas solicitações por 30 minutos.
     * Permite 3 tentativas, bloqueia a partir da 4ª.
     */
    private void checkTemporaryBlock(String username) {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(BLOCK_DURATION_MINUTES);

        long recentRequests = tokenRepository.countRecentRequestsByUsername(username, thirtyMinutesAgo);

        if (recentRequests >= MAX_REQUESTS_BEFORE_BLOCK) {
            log.warn("Usuário {} bloqueado temporariamente por excesso de solicitações de reset ({} tentativas)",
                    username, recentRequests);
            throw createException(ErrorEnum.ERROR_RESET_BLOCKED_TEMPORARILY);
        }

        log.debug("Usuário {} tem {} solicitações recentes de {} permitidas",
                username, recentRequests, MAX_REQUESTS_BEFORE_BLOCK);
    }

    @Override
    @Transactional
    public boolean validateResetCode(PasswordResetValidateRequest request) {
        String username = request.getUsername();
        String code = request.getCode();

        // Busca o token válido
        PasswordResetTokenEntity token = tokenRepository.findValidTokenByUsername(username, code)
                .orElseThrow(() -> {
                    log.warn("Código de reset inválido para usuário: {}", username);
                    return createException(ErrorEnum.ERROR_RESET_CODE_INVALID);
                });

        // Verifica se expirou
        if (token.isExpired()) {
            log.warn("Código de reset expirado para usuário: {}", username);
            throw createException(ErrorEnum.ERROR_RESET_CODE_EXPIRED);
        }

        // Marca como validado (permite o reset)
        token.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(token);

        log.info("Código de reset validado com sucesso para usuário: {}", username);
        return true;
    }

    @Override
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        String username = request.getUsername();
        String code = request.getCode();

        // Busca o token validado
        PasswordResetTokenEntity token = tokenRepository.findValidTokenByUsername(username, code)
                .orElseThrow(() -> {
                    log.warn("Código de reset não encontrado para usuário: {}", username);
                    return createException(ErrorEnum.ERROR_RESET_CODE_INVALID);
                });

        // Verifica se o código foi validado
        if (!token.isValidated()) {
            log.warn("Tentativa de reset sem validação prévia: {}", username);
            throw createException(ErrorEnum.ERROR_RESET_CODE_NOT_VALIDATED);
        }

        // Verifica se expirou
        if (token.isExpired()) {
            log.warn("Código de reset expirado para usuário: {}", username);
            throw createException(ErrorEnum.ERROR_RESET_CODE_EXPIRED);
        }

        // Valida a nova senha (formato)
        if (!PasswordUtils.isValid(request.getNewPassword())) {
            throw createException(ErrorEnum.ERROR_PASSWORD);
        }

        // Valida se as senhas coincidem
        if (!PasswordUtils.matches(request.getNewPassword(), request.getConfirmPassword())) {
            throw createException(ErrorEnum.ERROR_PASSWORD_MISMATCH);
        }

        UserEntity user = token.getUser();

        // Valida se a senha não foi usada nas últimas 5 vezes
        validatePasswordNotReused(user, request.getNewPassword());

        // Salva a senha atual no histórico antes de alterar
        savePasswordToHistory(user);

        // Atualiza a senha do usuário
        String newPasswordHash = PasswordUtils.encrypt(request.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Marca o token como usado
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        // Envia e-mail de confirmação de alteração de senha
        String userEmail = getUserEmail(user);
        sendPasswordChangedEmail(user, userEmail);

        log.info("Senha redefinida com sucesso para usuário: {}", username);
    }

    /**
     * Valida se a nova senha não foi utilizada nas últimas 5 vezes.
     */
    private void validatePasswordNotReused(UserEntity user, String newPassword) {
        // Verifica contra a senha atual
        if (BCrypt.checkpw(newPassword, user.getPasswordHash())) {
            log.warn("Usuário {} tentou reutilizar a senha atual", user.getUsername());
            throw createException(ErrorEnum.ERROR_RESET_PASSWORD_ALREADY_USED);
        }

        // Verifica contra as últimas 5 senhas do histórico
        List<PasswordHistoryEntity> lastPasswords = passwordHistoryRepository
                .findLastPasswordsByUserId(user.getId(), PASSWORD_HISTORY_SIZE);

        for (PasswordHistoryEntity history : lastPasswords) {
            if (BCrypt.checkpw(newPassword, history.getPasswordHash())) {
                log.warn("Usuário {} tentou reutilizar uma senha do histórico", user.getUsername());
                throw createException(ErrorEnum.ERROR_RESET_PASSWORD_ALREADY_USED);
            }
        }
    }

    /**
     * Salva a senha atual no histórico antes de alterá-la.
     */
    private void savePasswordToHistory(UserEntity user) {
        PasswordHistoryEntity history = new PasswordHistoryEntity();
        history.setId(UUID.randomUUID().toString());
        history.setUser(user);
        history.setPasswordHash(user.getPasswordHash());
        passwordHistoryRepository.save(history);

        log.debug("Senha salva no histórico para usuário: {}", user.getUsername());
    }

    /**
     * Gera um código numérico de 6 dígitos.
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Obtém o e-mail do usuário a partir dos contatos.
     */
    private String getUserEmail(UserEntity user) {
        return user.getContacts().stream()
                .filter(c -> c.getContactType() == ContactType.EMAIL)
                .findFirst()
                .map(ContactEntity::getValue)
                .orElse(user.getUsername());
    }

    /**
     * Envia o e-mail com o código de reset de senha.
     */
    private void sendResetCodeEmail(UserEntity user, String email, String code) {
        try {
            String htmlTemplate = getEmailTemplate();
            String userName = getUserName(user);

            String htmlContent = htmlTemplate
                    .replace("{{NOME_USUARIO}}", userName)
                    .replace("{{CODIGO_RESET}}", code)
                    .replace("{{MINUTOS_EXPIRACAO}}", String.valueOf(CODE_EXPIRATION_MINUTES));

            SendMailRequest mailRequest = SendMailRequest.builder()
                    .to(email)
                    .subject("Código de Recuperação de Senha - Auto Car")
                    .content(htmlContent)
                    .build();

            sendMailService.sendMail(mailRequest);
            log.info("E-mail de reset de senha enviado para: {}", email);

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de reset: {}", e.getMessage(), e);
            throw createException(ErrorEnum.ERROR_RESET_SEND_EMAIL);
        }
    }

    /**
     * Envia e-mail confirmando que a senha foi alterada com sucesso.
     */
    private void sendPasswordChangedEmail(UserEntity user, String email) {
        try {
            String htmlTemplate = getPasswordChangedEmailTemplate();
            String userName = getUserName(user);
            String changeDateTime = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));

            String htmlContent = htmlTemplate
                    .replace("{{NOME_USUARIO}}", userName)
                    .replace("{{DATA_HORA_ALTERACAO}}", changeDateTime);

            SendMailRequest mailRequest = SendMailRequest.builder()
                    .to(email)
                    .subject("Sua senha foi alterada - Auto Car")
                    .content(htmlContent)
                    .build();

            sendMailService.sendMail(mailRequest);
            log.info("E-mail de confirmação de alteração de senha enviado para: {}", email);

        } catch (Exception e) {
            // Não lança exceção aqui para não impedir o fluxo principal
            // A senha já foi alterada com sucesso
            log.error("Erro ao enviar e-mail de confirmação de alteração: {}", e.getMessage(), e);
        }
    }

    private String getEmailTemplate() {
        return parameterRepository.findByParamKey(PARAM_KEY_RESET_EMAIL)
                .map(param -> param.getParamValue())
                .orElseThrow(() -> {
                    log.error("Template de e-mail de reset de senha não encontrado: {}", PARAM_KEY_RESET_EMAIL);
                    return createException(ErrorEnum.ERROR_RESET_SEND_EMAIL);
                });
    }

    /**
     * Obtém o template de e-mail de confirmação de alteração de senha.
     */
    private String getPasswordChangedEmailTemplate() {
        return parameterRepository.findByParamKey(PARAM_KEY_PASSWORD_CHANGED_EMAIL)
                .map(param -> param.getParamValue())
                .orElseThrow(() -> {
                    log.error("Template de e-mail de confirmação de alteração não encontrado: {}", PARAM_KEY_PASSWORD_CHANGED_EMAIL);
                    return createException(ErrorEnum.ERROR_RESET_SEND_EMAIL);
                });
    }

    private String getUserName(UserEntity user) {
        if (user.getPerson() != null) {
            return user.getPerson().getFullName();
        } else if (user.getCompanyUser() != null && user.getCompanyUser().getCompany() != null) {
            return user.getCompanyUser().getCompany().getLegalName();
        }
        return user.getUsername();
    }

    private ClientRequestException createException(ErrorEnum errorEnum) {
        return new ClientRequestException(
                errorEnum.getHttpStatus(),
                errorEnum.getCode(),
                errorEnum.getTitle(),
                errorEnum.getMessage()
        );
    }
}
