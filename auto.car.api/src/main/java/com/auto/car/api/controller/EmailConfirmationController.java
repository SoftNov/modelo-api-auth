package com.auto.car.api.controller;

import com.auto.car.api.service.EmailConfirmationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para confirmação de e-mail.
 * Endpoints públicos - não requerem autenticação.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Confirmação de E-mail", description = "Endpoints para confirmação de e-mail de usuários")
public class EmailConfirmationController {

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    /**
     * Endpoint para confirmar o e-mail do usuário através do token.
     * GET /api/users/confirm?token={token}
     */
    @GetMapping("/confirm")
    @Operation(summary = "Confirmar e-mail", description = "Confirma o e-mail do usuário através do token enviado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "E-mail confirmado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Token inválido, expirado ou já utilizado")
    })
    public ResponseEntity<Map<String, String>> confirmEmail(@RequestParam("token") String token) {
        emailConfirmationService.confirmEmail(token);

        return ResponseEntity.ok(Map.of(
                "message", "E-mail confirmado com sucesso! Sua conta está ativa."
        ));
    }

    /**
     * Endpoint para reenviar o e-mail de confirmação.
     * POST /api/users/resend-confirmation?userId={userId}
     */
    @PostMapping("/resend-confirmation")
    @Operation(summary = "Reenviar confirmação", description = "Reenvia o e-mail de confirmação para o usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "E-mail reenviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "E-mail já confirmado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, String>> resendConfirmation(@RequestParam("userId") String userId) {
        emailConfirmationService.resendConfirmationEmail(userId);

        return ResponseEntity.ok(Map.of(
                "message", "Um novo e-mail de confirmação foi enviado."
        ));
    }
}
