package com.auto.car.api.controller;

import com.auto.car.api.dto.request.PasswordResetConfirmRequest;
import com.auto.car.api.dto.request.PasswordResetRequest;
import com.auto.car.api.dto.request.PasswordResetValidateRequest;
import com.auto.car.api.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller para o fluxo de reset de senha.
 * Endpoints públicos - não requerem autenticação.
 */
@RestController
@RequestMapping("/v1/auth/password")
@Tag(name = "Recuperação de Senha", description = "Endpoints para recuperação/reset de senha")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request")
    @Operation(
        summary = "Solicitar código de reset",
        description = "Envia um código de 6 dígitos para o e-mail do usuário. O código expira em 15 minutos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código enviado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "429", description = "Muitas solicitações - aguarde alguns minutos")
    })
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) {

        passwordResetService.requestPasswordReset(request);

        return ResponseEntity.ok(Map.of(
            "message", "Código de verificação enviado para seu e-mail.",
            "info", "O código expira em 15 minutos."
        ));
    }

    @PostMapping("/validate")
    @Operation(
        summary = "Validar código de reset",
        description = "Valida o código de 6 dígitos enviado por e-mail. Necessário antes de redefinir a senha."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Código validado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Código inválido ou expirado")
    })
    public ResponseEntity<Map<String, Object>> validateResetCode(
            @Valid @RequestBody PasswordResetValidateRequest request) {

        boolean valid = passwordResetService.validateResetCode(request);

        return ResponseEntity.ok(Map.of(
            "valid", valid,
            "message", "Código validado com sucesso. Agora você pode redefinir sua senha."
        ));
    }

    @PostMapping("/confirm")
    @Operation(
        summary = "Redefinir senha",
        description = "Redefine a senha do usuário após a validação do código. A nova senha deve ter no mínimo 8 caracteres, contendo letras, números e caracteres especiais."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
        @ApiResponse(responseCode = "400", description = "Código não validado, expirado ou senha inválida")
    })
    public ResponseEntity<Map<String, String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest request) {

        passwordResetService.confirmPasswordReset(request);

        return ResponseEntity.ok(Map.of(
            "message", "Senha redefinida com sucesso!",
            "info", "Você já pode fazer login com sua nova senha."
        ));
    }
}

