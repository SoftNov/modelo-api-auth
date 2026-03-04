package com.auto.car.api.controller;

import com.auto.car.api.dto.request.ContactRequest;
import com.auto.car.api.dto.request.UserUpdateRequest;
import com.auto.car.api.dto.response.UserProfileResponse;
import com.auto.car.api.service.UserEditService;
import com.auto.car.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para gerenciamento do próprio perfil do usuário autenticado.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento do próprio perfil")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserEditService userEditService;

    // ============================================
    // ENDPOINTS DE CONSULTA
    // ============================================

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
    @PreAuthorize("hasAnyRole('BUYER', 'SELLER', 'ADMIN', 'USER')")
    @Operation(summary = "Perfil do usuário", description = "Retorna o perfil completo do usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<UserProfileResponse> getProfile(
            @Parameter(description = "ID do cliente (usuário logado)", required = true)
            @RequestHeader("X-Client-Id") String clientId) {
        UserProfileResponse profile = userService.getUserProfile(clientId);
        return ResponseEntity.ok(profile);
    }

    // ============================================
    // ENDPOINTS DE EDIÇÃO - PRÓPRIO USUÁRIO
    // ============================================

    /**
     * Atualiza os dados do próprio usuário logado.
     * Permite editar: contatos, endereço e dados da empresa (apenas PJ).
     * NÃO permite editar: nome, CPF, data de nascimento (PF).
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Atualizar próprio perfil",
               description = "Atualiza os dados do usuário logado. Contatos e endereço são editáveis. " +
                           "Para empresas, razão social e nome fantasia também são editáveis. " +
                           "Dados pessoais (nome, CPF, data de nascimento) NÃO podem ser alterados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<UserProfileResponse> updateOwnProfile(
            @Parameter(description = "ID do cliente (usuário logado)", required = true)
            @RequestHeader("X-Client-Id") String clientId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse profile = userEditService.updateOwnProfile(clientId, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Adiciona um novo contato ao usuário logado.
     */
    @PostMapping("/profile/contacts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Adicionar contato", description = "Adiciona um novo contato (email ou telefone) ao usuário logado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato adicionado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "409", description = "Contato já existe")
    })
    public ResponseEntity<UserProfileResponse> addContact(
            @Parameter(description = "ID do cliente (usuário logado)", required = true)
            @RequestHeader("X-Client-Id") String clientId,
            @Valid @RequestBody ContactRequest request) {
        UserProfileResponse profile = userEditService.addContact(clientId, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Remove um contato do usuário logado.
     */
    @DeleteMapping("/profile/contacts/{contactId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remover contato", description = "Remove um contato do usuário logado. Não é possível remover o último e-mail.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contato removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "Não é possível remover o último e-mail"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<UserProfileResponse> removeContact(
            @Parameter(description = "ID do cliente (usuário logado)", required = true)
            @RequestHeader("X-Client-Id") String clientId,
            @Parameter(description = "ID do contato a ser removido", required = true)
            @PathVariable String contactId) {
        UserProfileResponse profile = userEditService.removeContact(clientId, contactId);
        return ResponseEntity.ok(profile);
    }

    // ============================================
    // ENDPOINTS ESPECÍFICOS POR PERFIL
    // ============================================

    /**
     * Endpoint acessível apenas por SELLER.
     */
    @GetMapping("/seller/dashboard")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
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
}
