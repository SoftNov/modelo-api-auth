package com.auto.car.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para validar o código de reset de senha.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para validar código de reset de senha")
public class PasswordResetValidateRequest {

    @NotBlank(message = "O e-mail/username é obrigatório")
    @Schema(description = "E-mail ou username do usuário", example = "usuario@email.com")
    private String username;

    @NotBlank(message = "O código é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos")
    @Schema(description = "Código de 6 dígitos enviado por e-mail", example = "123456")
    private String code;
}

