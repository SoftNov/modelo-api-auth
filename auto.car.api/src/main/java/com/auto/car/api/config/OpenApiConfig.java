package com.auto.car.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_KEY = "bearer-key";
    private static final String CLIENT_ID_KEY = "X-Client-Id";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(BEARER_KEY)
                        .addList(CLIENT_ID_KEY))
                .components(
                        new Components()
                                // Esquema de autenticação Bearer JWT
                                .addSecuritySchemes(BEARER_KEY,
                                        new SecurityScheme()
                                                .name(BEARER_KEY)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Token JWT obtido no login")
                                )
                                // Esquema para o header X-Client-Id
                                .addSecuritySchemes(CLIENT_ID_KEY,
                                        new SecurityScheme()
                                                .name(CLIENT_ID_KEY)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .description("ID do usuário retornado no login (userId)")
                                )
                )
                .info(new Info()
                        .title("auto.car.api - API")
                        .version("0.0.1")
                        .description("API marketplace auto car - sistema de compra e venda de veículos\n\n" +
                                "**Autenticação:**\n" +
                                "1. Faça login em `/v1/auth/login`\n" +
                                "2. Use o `accessToken` no header `Authorization: Bearer <token>`\n" +
                                "3. Use o `userId` retornado no header `X-Client-Id`"));
    }

    /**
     * Customiza as operações para adicionar o header X-Client-Id
     * em endpoints que requerem autenticação.
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            // Adiciona o parâmetro X-Client-Id para endpoints que não são públicos
            String path = handlerMethod.getMethod().getDeclaringClass().getSimpleName();

            // Não adiciona em controllers de autenticação e registro
            if (!path.contains("Auth") && !path.contains("Registration") && !path.contains("EmailConfirmation")) {
                Parameter clientIdParam = new Parameter()
                        .name("X-Client-Id")
                        .description("ID do usuário (retornado no login)")
                        .in("header")
                        .required(true)
                        .example("abc-123-def-456");

                operation.addParametersItem(clientIdParam);
            }

            return operation;
        };
    }
}
