package com.auto.car.api.service.impl;

import com.auto.car.api.dto.request.LoginRequest;
import com.auto.car.api.dto.request.RefreshRequest;
import com.auto.car.api.dto.response.AuthResponse;
import com.auto.car.api.enums.UserStatus;
import com.auto.car.api.enums.error.ErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.repository.UserRepository;
import com.auto.car.api.repository.entity.CompanyUserEntity;
import com.auto.car.api.repository.entity.UserEntity;
import com.auto.car.api.security.JwtTokenProvider;
import com.auto.car.api.security.JwtUserData;
import com.auto.car.api.service.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        try {
            // Verifica se o usuário existe e está com e-mail verificado
            UserEntity user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ClientRequestException(
                            HttpStatus.UNAUTHORIZED,
                            ErrorEnum.ERROR_INVALID_CREDENTIALS.getCode(),
                            ErrorEnum.ERROR_INVALID_CREDENTIALS.getTitle(),
                            ErrorEnum.ERROR_INVALID_CREDENTIALS.getMessage()
                    ));

            // Verifica se o e-mail foi confirmado
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                throw new ClientRequestException(
                        HttpStatus.FORBIDDEN,
                        ErrorEnum.ERROR_EMAIL_NOT_VERIFIED.getCode(),
                        ErrorEnum.ERROR_EMAIL_NOT_VERIFIED.getTitle(),
                        ErrorEnum.ERROR_EMAIL_NOT_VERIFIED.getMessage()
                );
            }

            // Verifica se o usuário está bloqueado
            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new ClientRequestException(
                        HttpStatus.FORBIDDEN,
                        ErrorEnum.ERROR_USER_BLOCKED.getCode(),
                        ErrorEnum.ERROR_USER_BLOCKED.getTitle(),
                        ErrorEnum.ERROR_USER_BLOCKED.getMessage()
                );
            }

            // Realiza a autenticação
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Monta os dados do usuário para criptografar no JWT
            JwtUserData userData = buildUserData(user);

            // Gera os tokens com dados criptografados
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails, userData);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            // Atualiza o último login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("Usuário {} autenticado com sucesso", request.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                    .userId(user.getId()) // Retorna o userId para o cliente usar no header
                    .build();

        } catch (ClientRequestException e) {
            throw e;
        } catch (BadCredentialsException e) {
            log.warn("Credenciais inválidas para usuário: {}", request.getUsername());
            throw new ClientRequestException(
                    HttpStatus.UNAUTHORIZED,
                    ErrorEnum.ERROR_INVALID_CREDENTIALS.getCode(),
                    ErrorEnum.ERROR_INVALID_CREDENTIALS.getTitle(),
                    ErrorEnum.ERROR_INVALID_CREDENTIALS.getMessage()
            );
        } catch (DisabledException e) {
            log.warn("Usuário desabilitado: {}", request.getUsername());
            throw new ClientRequestException(
                    HttpStatus.FORBIDDEN,
                    ErrorEnum.ERROR_USER_BLOCKED.getCode(),
                    ErrorEnum.ERROR_USER_BLOCKED.getTitle(),
                    ErrorEnum.ERROR_USER_BLOCKED.getMessage()
            );
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário: {}", e.getMessage(), e);
            throw new ClientRequestException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorEnum.ERROR_GENERIC.getCode(),
                    ErrorEnum.ERROR_GENERIC.getTitle(),
                    ErrorEnum.ERROR_GENERIC.getMessage()
            );
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Valida o refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                throw new ClientRequestException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getCode(),
                        ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getTitle(),
                        ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getMessage()
                );
            }

            // Verifica se é um refresh token
            if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
                throw new ClientRequestException(
                        HttpStatus.UNAUTHORIZED,
                        ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getCode(),
                        ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getTitle(),
                        ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getMessage()
                );
            }

            // Extrai o username do token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            // Busca o usuário para obter os dados atualizados
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ClientRequestException(
                            HttpStatus.UNAUTHORIZED,
                            ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getCode(),
                            ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getTitle(),
                            ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getMessage()
                    ));

            // Carrega o usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Monta os dados do usuário para criptografar no JWT
            JwtUserData userData = buildUserData(user);

            // Gera novos tokens com dados criptografados
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails, userData);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            log.info("Token renovado para usuário: {}", username);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                    .userId(user.getId())
                    .build();

        } catch (ClientRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao renovar token: {}", e.getMessage(), e);
            throw new ClientRequestException(
                    HttpStatus.UNAUTHORIZED,
                    ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getCode(),
                    ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getTitle(),
                    ErrorEnum.ERROR_INVALID_REFRESH_TOKEN.getMessage()
            );
        }
    }

    /**
     * Monta os dados do usuário para serem criptografados no JWT.
     * Inclui ID, nome e documento (CPF ou CNPJ).
     */
    private JwtUserData buildUserData(UserEntity user) {
        JwtUserData.JwtUserDataBuilder builder = JwtUserData.builder()
                .userId(user.getId());

        // Verifica se é pessoa física (tem Person)
        if (user.getPerson() != null) {
            builder.name(user.getPerson().getFullName())
                   .document(user.getPerson().getCpf())
                   .userType("PERSON");
        }
        // Verifica se é pessoa jurídica (tem CompanyUser)
        else if (user.getCompanyUser() != null && user.getCompanyUser().getCompany() != null) {
            builder.name(user.getCompanyUser().getCompany().getLegalName())
                   .document(user.getCompanyUser().getCompany().getCnpj())
                   .userType("COMPANY");
        }
        // Fallback para username
        else {
            builder.name(user.getUsername())
                   .document("")
                   .userType("UNKNOWN");
        }

        return builder.build();
    }
}
