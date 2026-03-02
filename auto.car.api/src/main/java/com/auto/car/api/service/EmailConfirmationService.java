package com.auto.car.api.service;

import com.auto.car.api.repository.entity.UserEntity;

public interface EmailConfirmationService {

    /**
     * Cria um token de confirmação de e-mail para o usuário.
     * O token expira em 24 horas.
     *
     * @param userId ID do usuário
     * @return Token gerado
     */
    String createConfirmationToken(String userId);

    /**
     * Confirma o e-mail do usuário através do token.
     *
     * @param token Token de confirmação
     */
    void confirmEmail(String token);

    /**
     * Reenvia o e-mail de confirmação para o usuário.
     *
     * @param userId ID do usuário
     */
    void resendConfirmationEmail(String userId);

    /**
     * Envia o e-mail de confirmação para o usuário.
     *
     * @param user  Entidade do usuário
     * @param token Token de confirmação
     */
    void sendConfirmationEmail(UserEntity user, String token);
}
