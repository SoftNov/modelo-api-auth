package com.auto.car.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para redefinir a senha após validação do código.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para redefinir a senha")
public class PasswordResetConfirmRequest {

    @NotBlank(message = "O e-mail/username é obrigatório")
    @Schema(description = "E-mail ou username do usuário", example = "usuario@email.com")
    private String username;

    @NotBlank(message = "O código é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos")
    @Schema(description = "Código de 6 dígitos validado anteriormente", example = "123456")
    private String code;

    @NotBlank(message = "A nova senha é obrigatória")
    @Schema(description = "Nova senha (mínimo 8 caracteres, letras, números e caracteres especiais)", example = "NovaSenha@123")
    private String newPassword;

    @NotBlank(message = "A confirmação da senha é obrigatória")
    @Schema(description = "Confirmação da nova senha", example = "NovaSenha@123")
    private String confirmPassword;
}

