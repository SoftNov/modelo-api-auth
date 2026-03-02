package com.auto.car.api.service;

import com.auto.car.api.dto.request.LoginRequest;
import com.auto.car.api.dto.request.RefreshRequest;
import com.auto.car.api.dto.response.AuthResponse;

public interface AuthService {

    /**
     * Realiza a autenticação do usuário e retorna os tokens JWT.
     *
     * @param request Dados de login (username e password)
     * @return Tokens de acesso e refresh
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Atualiza o token de acesso usando o refresh token.
     *
     * @param request Refresh token
     * @return Novos tokens de acesso e refresh
     */
    AuthResponse refreshToken(RefreshRequest request);
}

