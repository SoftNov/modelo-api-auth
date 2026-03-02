package com.auto.car.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para solicitar o código de reset de senha.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para solicitar código de reset de senha")
public class PasswordResetRequest {

    @NotBlank(message = "O e-mail/username é obrigatório")
    @Schema(description = "E-mail ou username do usuário", example = "usuario@email.com")
    private String username;
}

