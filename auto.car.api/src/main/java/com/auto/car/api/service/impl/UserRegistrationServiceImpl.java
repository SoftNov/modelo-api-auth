package com.auto.car.api.service.impl;

import com.auto.car.api.dto.UserDto;
import com.auto.car.api.dto.response.UserResponse;
import com.auto.car.api.enums.CompanyStatus;
import com.auto.car.api.enums.CompanyUserRole;
import com.auto.car.api.enums.OwnerType;
import com.auto.car.api.enums.RoleType;
import com.auto.car.api.enums.UserStatus;
import com.auto.car.api.enums.error.ErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.mapper.GenericMapper;
import com.auto.car.api.repository.CompanyRepository;
import com.auto.car.api.repository.EmailConfirmationTokenRepository;
import com.auto.car.api.repository.RoleRepository;
import com.auto.car.api.repository.UserRepository;
import com.auto.car.api.repository.entity.AddressEntity;
import com.auto.car.api.repository.entity.CompanyEntity;
import com.auto.car.api.repository.entity.CompanyUserEntity;
import com.auto.car.api.repository.entity.ContactEntity;
import com.auto.car.api.repository.entity.EmailConfirmationTokenEntity;
import com.auto.car.api.repository.entity.PersonEntity;
import com.auto.car.api.repository.entity.RoleEntity;
import com.auto.car.api.repository.entity.UserEntity;
import com.auto.car.api.service.EmailConfirmationService;
import com.auto.car.api.service.UserRegistrationService;
import com.auto.car.api.util.DocumentValidator;
import com.auto.car.api.util.PasswordUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GenericMapper mapper;

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    @Autowired
    private EmailConfirmationTokenRepository tokenRepository;

    @Override
    @Transactional
    public UserResponse register(UserDto userDto) {
        // Verifica se o usuário já existe
        checkExistingUser(userDto.getUsername());

        if (userDto.getCompany() != null)
            userDto = registerCompany(userDto);
        else
            userDto = registerPerson(userDto);

        // Gera ID do usuário se não existir
        if (userDto.getId() == null) {
            userDto.setId(UUID.randomUUID().toString());
        }

        // Persiste no banco
        var saved = saveUser(userDto);

        // Cria token e envia e-mail de confirmação de conta
        String token = emailConfirmationService.createConfirmationToken(saved.getId());
        emailConfirmationService.sendConfirmationEmail(saved, token);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .name(userDto.getCompany() != null ? userDto.getCompany().getLegalName() : userDto.getPerson().getFullName())
                .build();
    }

    /**
     * Verifica se o usuário já existe e trata conforme seu status:
     * - ACTIVE: lança erro informando que usuário já está ativo
     * - BLOCKED: lança erro informando que usuário está bloqueado
     * - PENDING_VERIFICATION: verifica se o token expirou e reenvia link de ativação
     */
    private void checkExistingUser(String username) {
        Optional<UserEntity> existingUser = userRepository.findByUsername(username);

        if (existingUser.isEmpty()) {
            return; // Usuário não existe, pode prosseguir com cadastro
        }

        UserEntity user = existingUser.get();

        switch (user.getStatus()) {
            case ACTIVE:
                log.warn("Tentativa de cadastro com usuário já ativo: {}", username);
                throwValidationError(ErrorEnum.ERROR_USER_ALREADY_ACTIVE);
                break;

            case BLOCKED:
                log.warn("Tentativa de cadastro com usuário bloqueado: {}", username);
                throwValidationError(ErrorEnum.ERROR_USER_BLOCKED);
                break;

            case PENDING_VERIFICATION:
                handlePendingVerificationUser(user);
                break;

            default:
                log.warn("Status de usuário desconhecido: {}", user.getStatus());
                throwValidationError(ErrorEnum.ERROR_USER_ALREADY_ACTIVE);
        }
    }

    /**
     * Trata usuário com status PENDING_VERIFICATION:
     * - Se o token ainda não expirou: informa que já existe cadastro pendente
     * - Se o token expirou: reenvia novo link de ativação
     */
    private void handlePendingVerificationUser(UserEntity user) {
        Optional<EmailConfirmationTokenEntity> latestToken = tokenRepository.findLatestByUserId(user.getId());

        if (latestToken.isPresent() && !latestToken.get().isExpired() && !latestToken.get().isUsed()) {
            // Token ainda válido - informa que já existe cadastro pendente
            log.info("Usuário {} já possui cadastro pendente com token válido", user.getUsername());
            throw new ClientRequestException(
                    ErrorEnum.ERROR_USER_PENDING_RESEND.getHttpStatus(),
                    ErrorEnum.ERROR_USER_PENDING_RESEND.getCode(),
                    "Cadastro pendente de verificação!",
                    "Já existe um cadastro pendente de verificação para este usuário. Verifique seu e-mail."
            );
        }

        // Token expirado ou não existe - reenvia novo link de ativação
        log.info("Reenviando link de ativação para usuário {} com token expirado", user.getUsername());
        String newToken = emailConfirmationService.createConfirmationToken(user.getId());
        emailConfirmationService.sendConfirmationEmail(user, newToken);

        throw new ClientRequestException(
                ErrorEnum.ERROR_USER_PENDING_RESEND.getHttpStatus(),
                ErrorEnum.ERROR_USER_PENDING_RESEND.getCode(),
                ErrorEnum.ERROR_USER_PENDING_RESEND.getTitle(),
                ErrorEnum.ERROR_USER_PENDING_RESEND.getMessage()
        );
    }

    private UserDto registerPerson(UserDto userCoreDto) {
        validatePersonData(userCoreDto);
        validateCommonUserData(userCoreDto);

        // Criptografa a senha após validação
        String encryptedPassword = validateAndEncryptPassword(userCoreDto);
        userCoreDto.setPassword(encryptedPassword);
        userCoreDto.setConfirmPassword(null); // Limpa confirmação por segurança

        return userCoreDto;
    }

    private UserDto registerCompany(UserDto userCoreDto) {
        validateCompanyData(userCoreDto);
        validateCommonUserData(userCoreDto);

        // Criptografa a senha após validação
        String encryptedPassword = validateAndEncryptPassword(userCoreDto);
        userCoreDto.setPassword(encryptedPassword);
        userCoreDto.setConfirmPassword(null); // Limpa confirmação por segurança

        return userCoreDto;
    }

    /**
     * Valida dados específicos de pessoa física
     */
    private void validatePersonData(UserDto userDto) {
        // Validação do CPF
        if (!DocumentValidator.isValidCPF(userDto.getPerson().getCpf())) {
            throwValidationError(ErrorEnum.ERROR_CPF);
        }

        // Validação da data de nascimento
        if (!DocumentValidator.isValidBirthDate(userDto.getPerson().getBirthDate())) {
            throwValidationError(ErrorEnum.ERROR_BIRTH_DATE);
        }
    }

    /**
     * Valida dados específicos de pessoa jurídica
     */
    private void validateCompanyData(UserDto userDto) {
        // Validação do CNPJ
        if (!DocumentValidator.isValidCNPJ(userDto.getCompany().getCnpj())) {
            throwValidationError(ErrorEnum.ERROR_CNPJ);
        }
    }

    /**
     * Valida dados comuns tanto para pessoa física quanto jurídica
     */
    private void validateCommonUserData(UserDto userDto) {
        // Validação do e-mail (obtido da lista de contatos)
        if (!DocumentValidator.isValidEmailFromContacts(userDto.getContacts())) {
            throwValidationError(ErrorEnum.ERROR_EMAIL);
        }
    }

    /**
     * Valida senha e confirmação de senha, retornando o hash criptografado
     */
    private String validateAndEncryptPassword(UserDto userDto) {
        // Validação da senha
        if (!PasswordUtils.isValid(userDto.getPassword())) {
            throwValidationError(ErrorEnum.ERROR_PASSWORD);
        }

        // Validação se as senhas coincidem
        if (!PasswordUtils.matches(userDto.getPassword(), userDto.getConfirmPassword())) {
            throwValidationError(ErrorEnum.ERROR_PASSWORD_MISMATCH);
        }

        // Retorna a senha criptografada usando BCrypt
        return PasswordUtils.encrypt(userDto.getPassword());
    }

    /**
     * Função específica para lançar erros de validação
     */
    private void throwValidationError(ErrorEnum errorEnum) {
        throw new ClientRequestException(
                errorEnum.getHttpStatus(),
                errorEnum.getCode(),
                errorEnum.getTitle(),
                errorEnum.getMessage()
        );
    }

    private UserEntity saveUser(UserDto userDto) {
        // Mapeamento base do UserEntity
        UserEntity user = mapper.map(userDto, UserEntity.class);
        user.setId(userDto.getId());
        user.setPasswordHash(userDto.getPassword()); // já está criptografada
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        // Pessoa Física (PF)
        if (userDto.getPerson() != null) {
            PersonEntity person = mapper.map(userDto.getPerson(), PersonEntity.class);
            person.setId(UUID.randomUUID().toString());
            person.setUser(user);
            user.setPerson(person);
        }

        // Contatos
        if (userDto.getContacts() != null) {
            user.getContacts().clear();
            for (var c : userDto.getContacts()) {
                if (c == null) continue;
                ContactEntity ce = mapper.map(c, ContactEntity.class);
                ce.setId(UUID.randomUUID().toString());
                ce.setOwnerType(OwnerType.USER);
                ce.setUser(user);
                user.getContacts().add(ce);
            }
        }

        // Endereço
        if (userDto.getAddress() != null) {
            AddressEntity ae = mapper.map(userDto.getAddress(), AddressEntity.class);
            ae.setId(UUID.randomUUID().toString());
            ae.setOwnerType(OwnerType.USER);
            ae.setUser(user);
            user.setAddresses(ae);
        }

        // Adiciona roles padrão: BUYER e SELLER
        assignDefaultRoles(user);

        // Salva o usuário primeiro (necessário para ter o ID no CompanyUser)
        user = userRepository.save(user);

        // Pessoa Jurídica (PJ) - salva Company com CompanyUser em cascade
        if (userDto.getCompany() != null) {
            CompanyEntity company = mapper.map(userDto.getCompany(), CompanyEntity.class);
            company.setId(UUID.randomUUID().toString());
            company.setStatus(CompanyStatus.PENDING_VERIFICATION);

            // Cria o vínculo entre User e Company
            CompanyUserEntity companyUser = new CompanyUserEntity();
            companyUser.setCompanyId(company.getId());
            companyUser.setUserId(user.getId());
            companyUser.setRole(CompanyUserRole.OWNER);

            // Adiciona na lista da Company - será salvo em cascade
            company.getCompanyUsers().add(companyUser);

            // Salva Company (CompanyUser será persistido em cascade)
            companyRepository.save(company);
        }

        return user;
    }

    /**
     * Atribui as roles padrão (BUYER e SELLER) ao usuário.
     */
    private void assignDefaultRoles(UserEntity user) {
        // Busca a role BUYER
        roleRepository.findByName(RoleType.BUYER.getRoleName())
                .ifPresent(role -> user.getRoles().add(role));

        // Busca a role SELLER
        roleRepository.findByName(RoleType.SELLER.getRoleName())
                .ifPresent(role -> user.getRoles().add(role));

        log.info("Roles padrão atribuídas ao usuário: BUYER, SELLER");
    }
}
