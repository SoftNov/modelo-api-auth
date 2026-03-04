package com.auto.car.api.service.impl;

import com.auto.car.api.dto.ParameterDto;
import com.auto.car.api.enums.ParameterCategory;
import com.auto.car.api.enums.error.ParameterErrorEnum;
import com.auto.car.api.exception.ClientRequestException;
import com.auto.car.api.mapper.GenericMapper;
import com.auto.car.api.repository.ParameterRepository;
import com.auto.car.api.repository.entity.ParameterEntity;
import com.auto.car.api.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service para operações de parâmetros do sistema.
 */
@Service
public class ParameterServiceImpl implements ParameterService {

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private GenericMapper mapper;

    @Override
    public ParameterDto create(ParameterDto parameterDto) {
        // Valida se já existe parâmetro com a mesma chave
        if (parameterRepository.existsByParamKey(parameterDto.getParamKey())) {
            throwError(ParameterErrorEnum.PARAMETER_ALREADY_EXISTS);
        }

        // Validações
        validateParameter(parameterDto);

        // Cria a entidade
        ParameterEntity entity = mapper.map(parameterDto, ParameterEntity.class);
        entity.setId(UUID.randomUUID().toString());

        // Salva no banco
        ParameterEntity savedEntity = parameterRepository.save(entity);

        return mapper.map(savedEntity, ParameterDto.class);
    }

    @Override
    public ParameterDto update(String id, ParameterDto parameterDto) {
        // Busca o parâmetro existente
        ParameterEntity existingEntity = parameterRepository.findById(id)
            .orElseThrow(() -> createException(ParameterErrorEnum.PARAMETER_NOT_FOUND));

        // Verifica se é editável
        if (!existingEntity.getIsEditable()) {
            throwError(ParameterErrorEnum.PARAMETER_NOT_EDITABLE);
        }

        // Validações
        validateParameter(parameterDto);

        // Atualiza os campos
        existingEntity.setParamValue(parameterDto.getParamValue());
        existingEntity.setParamType(parameterDto.getParamType());
        existingEntity.setCategory(parameterDto.getCategory());
        existingEntity.setDescription(parameterDto.getDescription());
        existingEntity.setIsActive(parameterDto.getIsActive());

        // Salva no banco
        ParameterEntity savedEntity = parameterRepository.save(existingEntity);

        return mapper.map(savedEntity, ParameterDto.class);
    }

    @Override
    public Optional<ParameterDto> findById(String id) {
        return parameterRepository.findById(id)
            .map(entity -> mapper.map(entity, ParameterDto.class));
    }

    @Override
    public Optional<ParameterDto> findByKey(String paramKey) {
        return parameterRepository.findByParamKeyAndIsActiveTrue(paramKey)
            .map(entity -> mapper.map(entity, ParameterDto.class));
    }

    @Override
    public List<ParameterDto> findAll() {
        return parameterRepository.findAll().stream()
            .map(entity -> mapper.map(entity, ParameterDto.class))
            .toList();
    }

    @Override
    public List<ParameterDto> findAllActive() {
        return parameterRepository.findByIsActiveTrue().stream()
            .map(entity -> mapper.map(entity, ParameterDto.class))
            .toList();
    }

    @Override
    public List<ParameterDto> findByCategory(ParameterCategory category) {
        return parameterRepository.findByCategoryAndIsActiveTrue(category).stream()
            .map(entity -> mapper.map(entity, ParameterDto.class))
            .toList();
    }

    @Override
    public Page<ParameterDto> findAllPaged(Pageable pageable) {
        return parameterRepository.findAll(pageable)
            .map(entity -> mapper.map(entity, ParameterDto.class));
    }

    @Override
    public Page<ParameterDto> findAllActivePaged(Pageable pageable) {
        return parameterRepository.findByIsActiveTrue(pageable)
            .map(entity -> mapper.map(entity, ParameterDto.class));
    }

    @Override
    public Page<ParameterDto> findByCategoryPaged(ParameterCategory category, Pageable pageable) {
        return parameterRepository.findByCategoryAndIsActiveTrue(category, pageable)
            .map(entity -> mapper.map(entity, ParameterDto.class));
    }

    @Override
    public void deactivate(String id) {
        ParameterEntity entity = parameterRepository.findById(id)
            .orElseThrow(() -> createException(ParameterErrorEnum.PARAMETER_NOT_FOUND));

        entity.setIsActive(false);
        parameterRepository.save(entity);
    }

    @Override
    public void activate(String id) {
        ParameterEntity entity = parameterRepository.findById(id)
            .orElseThrow(() -> createException(ParameterErrorEnum.PARAMETER_NOT_FOUND));

        entity.setIsActive(true);
        parameterRepository.save(entity);
    }

    @Override
    public void delete(String id) {
        if (!parameterRepository.existsById(id)) {
            throwError(ParameterErrorEnum.PARAMETER_NOT_FOUND);
        }
        parameterRepository.deleteById(id);
    }

    /**
     * Valida os dados do parâmetro.
     */
    private void validateParameter(ParameterDto parameterDto) {
        if (parameterDto.getParamKey() == null || parameterDto.getParamKey().trim().isEmpty()) {
            throwError(ParameterErrorEnum.PARAMETER_KEY_REQUIRED);
        }

        if (parameterDto.getParamValue() == null || parameterDto.getParamValue().trim().isEmpty()) {
            throwError(ParameterErrorEnum.PARAMETER_VALUE_REQUIRED);
        }

        if (parameterDto.getParamType() == null) {
            throwError(ParameterErrorEnum.PARAMETER_TYPE_REQUIRED);
        }

        if (parameterDto.getCategory() == null) {
            throwError(ParameterErrorEnum.PARAMETER_CATEGORY_REQUIRED);
        }
    }

    /**
     * Lança exceção de erro a partir do enum.
     */
    private void throwError(ParameterErrorEnum error) {
        throw new ClientRequestException(
            error.getHttpStatus(),
            error.getCode(),
            error.getTitle(),
            error.getMessage()
        );
    }

    /**
     * Cria exceção de erro a partir do enum (para uso em orElseThrow).
     */
    private ClientRequestException createException(ParameterErrorEnum error) {
        return new ClientRequestException(
            error.getHttpStatus(),
            error.getCode(),
            error.getTitle(),
            error.getMessage()
        );
    }
}
