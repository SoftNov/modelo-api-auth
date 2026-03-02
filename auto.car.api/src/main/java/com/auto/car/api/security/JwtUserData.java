package com.auto.car.api.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO que contém os dados do usuário que serão criptografados dentro do JWT.
 * Esses dados são usados para validação adicional de segurança.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUserData {

    @JsonProperty("id")
    private String userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("document")
    private String document; // CPF ou CNPJ

    @JsonProperty("type")
    private String userType; // PERSON ou COMPANY
}

