package com.auto.car.api.controller;

import com.auto.car.api.dto.request.RegisterUserRequest;
import com.auto.car.api.dto.response.UserResponse;
import com.auto.car.api.mapper.GenericMapper;
import com.auto.car.api.service.UserRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para registro de usuários.
 * Endpoints públicos - não requerem autenticação.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Registro de Usuários", description = "Endpoints para registro de novos usuários")
public class UserRegistrationController {

    @Autowired
    private UserRegistrationService registrationService;

    @Autowired
    private GenericMapper mapper;

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário (PF ou PJ) no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        var result = registrationService.register(request.getUser());
        return ResponseEntity.status(201).body(result);
    }
}
