package com.auto.car.api.service;

import com.auto.car.api.dto.ParameterDto;
import com.auto.car.api.enums.ParameterCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interface de porta de entrada para operações de parâmetros do sistema.
 */
public interface ParameterService {

    /**
     * Cria um novo parâmetro.
     */
    ParameterDto create(ParameterDto parameterDto);

    /**
     * Atualiza um parâmetro existente.
     */
    ParameterDto update(String id, ParameterDto parameterDto);

    /**
     * Busca um parâmetro pelo ID.
     */
    Optional<ParameterDto> findById(String id);

    /**
     * Busca um parâmetro pela chave.
     */
    Optional<ParameterDto> findByKey(String paramKey);

    /**
     * Lista todos os parâmetros.
     */
    List<ParameterDto> findAll();

    /**
     * Lista todos os parâmetros ativos.
     */
    List<ParameterDto> findAllActive();

    /**
     * Lista parâmetros por categoria.
     */
    List<ParameterDto> findByCategory(ParameterCategory category);

    /**
     * Lista todos os parâmetros com paginação.
     */
    Page<ParameterDto> findAllPaged(Pageable pageable);

    /**
     * Lista todos os parâmetros ativos com paginação.
     */
    Page<ParameterDto> findAllActivePaged(Pageable pageable);

    /**
     * Lista parâmetros por categoria com paginação.
     */
    Page<ParameterDto> findByCategoryPaged(ParameterCategory category, Pageable pageable);

    /**
     * Desativa um parâmetro.
     */
    void deactivate(String id);

    /**
     * Ativa um parâmetro.
     */
    void activate(String id);

    /**
     * Remove um parâmetro (hard delete).
     */
    void delete(String id);
}
