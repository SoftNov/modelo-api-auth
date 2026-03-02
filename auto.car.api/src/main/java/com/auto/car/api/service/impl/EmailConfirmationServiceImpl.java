package com.auto.car.api.service.impl;

import com.auto.car.api.enums.CompanyStatus;
import com.auto.car.api.enums.ContactType;
import com.auto.car.api.enums.UserStatus;
import com.auto.car.api.enums.error.ErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.repository.CompanyRepository;
import com.auto.car.api.repository.EmailConfirmationTokenRepository;
import com.auto.car.api.repository.UserRepository;
import com.auto.car.api.repository.entity.EmailConfirmationTokenEntity;
import com.auto.car.api.repository.entity.UserEntity;
import com.auto.car.api.service.EmailConfirmationService;
import com.auto.car.api.service.ParameterService;
import com.auto.car.api.service.SendMailService;
import com.auto.car.api.service.dto.SendMailRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Log4j2
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private static final String EMAIL_TEMPLATE_PARAM_KEY = "email-create-account-html";
    private static final String EMAIL_SUBJECT_PARAM_KEY = "email-create-account-subject";

    @Autowired
    private EmailConfirmationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private ParameterService parameterService;

    @Value("${app.confirmation.url}")
    private String confirmationBaseUrl;

    @Override
    @Transactional
    public String createConfirmationToken(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> throwError(ErrorEnum.ERROR_USER_NOT_FOUND));

        // Cria novo token
        EmailConfirmationTokenEntity token = new EmailConfirmationTokenEntity();
        token.setId(UUID.randomUUID().toString());
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));

        tokenRepository.save(token);

        log.info("Token de confirmação criado para usuário: {} - expira em: {}", userId, token.getExpiresAt());

        return token.getToken();
    }

    @Override
    @Transactional
    public void confirmEmail(String token) {
        EmailConfirmationTokenEntity confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> throwError(ErrorEnum.ERROR_TOKEN_INVALID));

        // Verifica se o token já foi utilizado
        if (confirmationToken.isUsed()) {
            throw throwError(ErrorEnum.ERROR_TOKEN_ALREADY_USED);
        }

        // Verifica se o token expirou
        if (confirmationToken.isExpired()) {
            throw throwError(ErrorEnum.ERROR_TOKEN_EXPIRED);
        }

        // Marca o token como utilizado
        confirmationToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(confirmationToken);

        // Atualiza o usuário
        UserEntity user = confirmationToken.getUser();
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // Se o usuário tem empresa vinculada, ativa a empresa também
        if (user.getCompanyUser() != null) {
            var company = user.getCompanyUser().getCompany();
            if (company != null && company.getStatus() == CompanyStatus.PENDING_VERIFICATION) {
                company.setStatus(CompanyStatus.ACTIVE);
                companyRepository.save(company);
                log.info("Empresa ativada: {}", company.getId());
            }
        }

        log.info("E-mail confirmado com sucesso para usuário: {}", user.getId());
    }

    @Override
    @Transactional
    public void resendConfirmationEmail(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> throwError(ErrorEnum.ERROR_USER_NOT_FOUND));

        // Verifica se o e-mail já foi confirmado
        if (user.getEmailVerifiedAt() != null) {
            throw throwError(ErrorEnum.ERROR_EMAIL_ALREADY_CONFIRMED);
        }

        // Cria novo token
        String token = createConfirmationToken(userId);

        // Envia o e-mail
        sendConfirmationEmail(user, token);

        log.info("E-mail de confirmação reenviado para usuário: {}", userId);
    }

    @Override
    public void sendConfirmationEmail(UserEntity user, String token) {
        try {
            // Busca o template de e-mail
            var templateParam = parameterService.findByKey(EMAIL_TEMPLATE_PARAM_KEY)
                    .orElse(null);

            if (templateParam == null) {
                log.warn("Template de e-mail não encontrado: {}", EMAIL_TEMPLATE_PARAM_KEY);
                throw throwError(ErrorEnum.ERROR_SEND_EMAIL);
            }

            // Busca o assunto do e-mail
            String subject = parameterService.findByKey(EMAIL_SUBJECT_PARAM_KEY)
                    .map(p -> p.getParamValue())
                    .orElse("Confirmação de Conta - Auto Car");

            // Obtém o nome do usuário
            String nomeUsuario = getUserName(user);

            // Gera o link de confirmação
            String linkConfirmacao = confirmationBaseUrl + "?token=" + token;

            // Substitui as variáveis no template
            String htmlContent = templateParam.getParamValue()
                    .replace("{{NOME_USUARIO}}", nomeUsuario)
                    .replace("{{LINK_CONFIRMACAO}}", linkConfirmacao);

            // Obtém o e-mail do usuário
            String emailDestinatario = getEmailFromUser(user);

            if (emailDestinatario == null) {
                log.warn("E-mail do usuário não encontrado nos contatos");
                throw throwError(ErrorEnum.ERROR_SEND_EMAIL);
            }

            // Monta e envia o e-mail
            SendMailRequest mailRequest = SendMailRequest.builder()
                    .to(emailDestinatario)
                    .subject(subject)
                    .content(htmlContent)
                    .build();

            sendMailService.sendMail(mailRequest);

            log.info("E-mail de confirmação enviado para: {}", emailDestinatario);

        } catch (ClientRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de confirmação: {}", e.getMessage(), e);
            throw throwError(ErrorEnum.ERROR_SEND_EMAIL);
        }
    }

    /**
     * Cria e retorna uma exceção com base no ErrorEnum.
     */
    private ClientRequestException throwError(ErrorEnum errorEnum) {
        return new ClientRequestException(
                errorEnum.getHttpStatus(),
                errorEnum.getCode(),
                errorEnum.getTitle(),
                errorEnum.getMessage()
        );
    }

    private String getUserName(UserEntity user) {
        if (user.getCompanyUser() != null && user.getCompanyUser().getCompany() != null) {
            return user.getCompanyUser().getCompany().getLegalName();
        }
        if (user.getPerson() != null) {
            return user.getPerson().getFullName();
        }
        return user.getUsername();
    }

    private String getEmailFromUser(UserEntity user) {
        if (user.getContacts() == null || user.getContacts().isEmpty()) {
            return null;
        }

        return user.getContacts().stream()
                .filter(contact -> contact.getContactType() == ContactType.EMAIL)
                .map(contact -> contact.getValue())
                .findFirst()
                .orElse(null);
    }
}
