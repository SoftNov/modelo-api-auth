package com.auto.car.api.security;

import com.auto.car.api.enums.error.ErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro que valida se o header "X-Client-Id" corresponde ao ID do usuário
 * contido nos dados criptografados do JWT.
 *
 * Esta é uma camada adicional de segurança que garante que:
 * 1. O usuário autenticado só pode acessar seus próprios recursos
 * 2. O JWT pertence realmente ao usuário que está fazendo a requisição
 */
@Component
@Order(2)
@Log4j2
public class ClientIdValidationFilter extends OncePerRequestFilter {

    private static final String CLIENT_ID_HEADER = "X-Client-Id";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Endpoints que NÃO precisam de validação do client-id
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/v1/auth/**",
            "/api/users/register",
            "/api/users/confirm",
            "/api/users/resend-confirmation",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Remove o context path do caminho da requisição
        if (StringUtils.hasText(contextPath) && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }

        // Verifica se o endpoint está na lista de exclusão
        if (isExcludedPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai o JWT do header Authorization
        String jwt = getJwtFromRequest(request);

        // Se não houver JWT, deixa o JwtAuthenticationFilter tratar
        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Valida o client-id
        try {
            validateClientId(request, jwt);
        } catch (ClientRequestException e) {
            log.warn("Validação de client-id falhou: {}", e.getMessage());
            sendErrorResponse(response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void validateClientId(HttpServletRequest request, String jwt) {
        String clientIdHeader = request.getHeader(CLIENT_ID_HEADER);

        // Verifica se o header client-id está presente
        if (!StringUtils.hasText(clientIdHeader)) {
            log.warn("Header {} não encontrado na requisição", CLIENT_ID_HEADER);
            throw new ClientRequestException(
                    ErrorEnum.ERROR_CLIENT_ID_MISSING.getHttpStatus(),
                    ErrorEnum.ERROR_CLIENT_ID_MISSING.getCode(),
                    ErrorEnum.ERROR_CLIENT_ID_MISSING.getTitle(),
                    ErrorEnum.ERROR_CLIENT_ID_MISSING.getMessage()
            );
        }

        // Extrai o ID do usuário dos dados criptografados do JWT
        String userIdFromToken = jwtTokenProvider.getUserIdFromToken(jwt);

        if (userIdFromToken == null) {
            log.warn("Não foi possível extrair o ID do usuário do token JWT");
            throw new ClientRequestException(
                    ErrorEnum.ERROR_INVALID_TOKEN_DATA.getHttpStatus(),
                    ErrorEnum.ERROR_INVALID_TOKEN_DATA.getCode(),
                    ErrorEnum.ERROR_INVALID_TOKEN_DATA.getTitle(),
                    ErrorEnum.ERROR_INVALID_TOKEN_DATA.getMessage()
            );
        }

        // Compara o client-id do header com o ID do JWT
        if (!clientIdHeader.equals(userIdFromToken)) {
            log.warn("Client-Id {} não corresponde ao ID do token {}", clientIdHeader, userIdFromToken);
            throw new ClientRequestException(
                    ErrorEnum.ERROR_CLIENT_ID_MISMATCH.getHttpStatus(),
                    ErrorEnum.ERROR_CLIENT_ID_MISMATCH.getCode(),
                    ErrorEnum.ERROR_CLIENT_ID_MISMATCH.getTitle(),
                    ErrorEnum.ERROR_CLIENT_ID_MISMATCH.getMessage()
            );
        }

        log.debug("Client-Id validado com sucesso para usuário: {}", userIdFromToken);
    }

    private boolean isExcludedPath(String requestPath) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, ClientRequestException e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"code\":\"%s\",\"title\":\"%s\",\"message\":\"%s\"}",
                e.getCode(),
                e.getTitle(),
                e.getMessage()
        );

        response.getWriter().write(jsonResponse);
    }
}

