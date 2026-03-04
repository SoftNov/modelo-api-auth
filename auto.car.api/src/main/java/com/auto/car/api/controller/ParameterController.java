package com.auto.car.api.controller;

import com.auto.car.api.dto.ParameterDto;
import com.auto.car.api.enums.ParameterCategory;
import com.auto.car.api.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de parâmetros do sistema.
 * Acesso restrito a ADMIN.
 */
@RestController
@RequestMapping("/api/parameters")
@Tag(name = "Parâmetros", description = "Endpoints para gerenciamento de parâmetros do sistema")
@PreAuthorize("hasRole('ADMIN')")
public class ParameterController {

    @Autowired
    private ParameterService parameterService;

    @PostMapping
    @Operation(summary = "Criar parâmetro", description = "Cria um novo parâmetro no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Parâmetro criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Parâmetro já existe")
    })
    public ResponseEntity<ParameterDto> create(@RequestBody ParameterDto parameterDto) {
        ParameterDto created = parameterService.create(parameterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar parâmetro", description = "Atualiza um parâmetro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parâmetro atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Parâmetro não editável"),
        @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado")
    })
    public ResponseEntity<ParameterDto> update(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do parâmetro") @PathVariable String id,
            @RequestBody ParameterDto parameterDto) {
        ParameterDto updated = parameterService.update(id, parameterDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar parâmetro por ID", description = "Busca um parâmetro pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parâmetro encontrado"),
        @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado")
    })
    public ResponseEntity<ParameterDto> findById(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do parâmetro") @PathVariable String id) {
        return parameterService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/key/{paramKey}")
    @Operation(summary = "Buscar parâmetro por chave", description = "Busca um parâmetro pela sua chave única")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parâmetro encontrado"),
        @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado")
    })
    public ResponseEntity<ParameterDto> findByKey(
            @io.swagger.v3.oas.annotations.Parameter(description = "Chave do parâmetro") @PathVariable String paramKey) {
        return parameterService.findByKey(paramKey)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar todos os parâmetros (paginado)",
               description = "Lista todos os parâmetros cadastrados com paginação. " +
                           "Parâmetros: page (página, default 0), size (itens por página, default 10), sort (campo de ordenação, default paramKey)")
    @ApiResponse(responseCode = "200", description = "Lista de parâmetros retornada com sucesso")
    public ResponseEntity<Page<ParameterDto>> findAll(
            @io.swagger.v3.oas.annotations.Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Campo de ordenação", example = "paramKey")
            @RequestParam(defaultValue = "paramKey") String sort,
            @io.swagger.v3.oas.annotations.Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ParameterDto> parameters = parameterService.findAllPaged(pageable);
        return ResponseEntity.ok(parameters);
    }

    @GetMapping("/active")
    @Operation(summary = "Listar parâmetros ativos (paginado)", description = "Lista todos os parâmetros ativos com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de parâmetros ativos retornada com sucesso")
    public ResponseEntity<Page<ParameterDto>> findAllActive(
            @io.swagger.v3.oas.annotations.Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Campo de ordenação", example = "paramKey")
            @RequestParam(defaultValue = "paramKey") String sort,
            @io.swagger.v3.oas.annotations.Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ParameterDto> parameters = parameterService.findAllActivePaged(pageable);
        return ResponseEntity.ok(parameters);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Listar parâmetros por categoria (paginado)", description = "Lista parâmetros de uma categoria específica com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de parâmetros da categoria retornada com sucesso")
    public ResponseEntity<Page<ParameterDto>> findByCategory(
            @io.swagger.v3.oas.annotations.Parameter(description = "Categoria do parâmetro") @PathVariable ParameterCategory category,
            @io.swagger.v3.oas.annotations.Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @io.swagger.v3.oas.annotations.Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @io.swagger.v3.oas.annotations.Parameter(description = "Campo de ordenação", example = "paramKey")
            @RequestParam(defaultValue = "paramKey") String sort,
            @io.swagger.v3.oas.annotations.Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ParameterDto> parameters = parameterService.findByCategoryPaged(category, pageable);
        return ResponseEntity.ok(parameters);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar parâmetro", description = "Desativa um parâmetro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Parâmetro desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado")
    })
    public ResponseEntity<Void> deactivate(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do parâmetro") @PathVariable String id) {
        parameterService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativar parâmetro", description = "Ativa um parâmetro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Parâmetro ativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado")
    })
    public ResponseEntity<Void> activate(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do parâmetro") @PathVariable String id) {
        parameterService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover parâmetro", description = "Remove permanentemente um parâmetro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Parâmetro removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Parâmetro não encontrado")
    })
    public ResponseEntity<Void> delete(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do parâmetro") @PathVariable String id) {
        parameterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
