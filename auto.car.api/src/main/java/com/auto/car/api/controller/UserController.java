package com.auto.car.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para gerenciamento de usuários autenticados.
 * Demonstra uso de roles a nível de método.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    /**
     * Endpoint acessível por qualquer usuário autenticado.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Dados do usuário logado", description = "Retorna os dados do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities()
        ));
    }

    /**
     * Endpoint acessível apenas por BUYER ou SELLER.
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER')")
    @Operation(summary = "Perfil do usuário", description = "Retorna o perfil do usuário (BUYER ou SELLER)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Map<String, String>> getProfile() {
        return ResponseEntity.ok(Map.of(
                "message", "Perfil do usuário"
        ));
    }

    /**
     * Endpoint acessível apenas por SELLER.
     */
    @GetMapping("/seller/dashboard")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Dashboard do vendedor", description = "Retorna dados do dashboard do vendedor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard retornado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas vendedores")
    })
    public ResponseEntity<Map<String, String>> getSellerDashboard() {
        return ResponseEntity.ok(Map.of(
                "message", "Dashboard do vendedor"
        ));
    }

    /**
     * Endpoint acessível apenas por BUYER.
     */
    @GetMapping("/buyer/favorites")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Favoritos do comprador", description = "Retorna lista de veículos favoritos do comprador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favoritos retornados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas compradores")
    })
    public ResponseEntity<Map<String, String>> getBuyerFavorites() {
        return ResponseEntity.ok(Map.of(
                "message", "Lista de favoritos do comprador"
        ));
    }

    /**
     * Endpoint acessível apenas por ADMIN.
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos usuários", description = "Lista todos os usuários do sistema (apenas ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    public ResponseEntity<Map<String, String>> getAllUsers() {
        return ResponseEntity.ok(Map.of(
                "message", "Lista de todos os usuários"
        ));
    }

    /**
     * Endpoint acessível por ADMIN ou MANAGER.
     */
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Alterar status do usuário", description = "Altera o status de um usuário (ADMIN ou MANAGER)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, String>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam String status) {
        return ResponseEntity.ok(Map.of(
                "message", "Status do usuário " + userId + " alterado para " + status
        ));
    }

    /**
     * Endpoint acessível por SUPPORT, MANAGER ou ADMIN.
     */
    @GetMapping("/{userId}/details")
    @PreAuthorize("hasAnyRole('SUPPORT', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Detalhes do usuário", description = "Retorna detalhes completos de um usuário (SUPPORT, MANAGER ou ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalhes retornados com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, String>> getUserDetails(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of(
                "message", "Detalhes do usuário " + userId
        ));
    }
}

