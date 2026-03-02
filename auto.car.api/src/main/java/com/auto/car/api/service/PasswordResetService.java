package com.auto.car.api.service;

import com.auto.car.api.dto.request.PasswordResetConfirmRequest;
import com.auto.car.api.dto.request.PasswordResetRequest;
import com.auto.car.api.dto.request.PasswordResetValidateRequest;

/**
 * Serviço para gerenciar o fluxo de reset de senha.
 */
public interface PasswordResetService {

    /**
     * Solicita o envio de código de reset de senha por e-mail.
     * O código tem validade de 15 minutos.
     *
     * @param request dados com o username/email do usuário
     */
    void requestPasswordReset(PasswordResetRequest request);

    /**
     * Valida o código de reset de senha enviado por e-mail.
     *
     * @param request dados com username e código
     * @return true se o código for válido
     */
    boolean validateResetCode(PasswordResetValidateRequest request);

    /**
     * Confirma o reset de senha após validação do código.
     *
     * @param request dados com username, código e nova senha
     */
    void confirmPasswordReset(PasswordResetConfirmRequest request);
}

