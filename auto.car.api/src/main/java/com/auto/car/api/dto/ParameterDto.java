package com.auto.car.api.dto;

import com.auto.car.api.enums.ParameterCategory;
import com.auto.car.api.enums.ParameterType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO que representa os parâmetros e configurações do sistema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterDto {

    @JsonIgnore
    private String id;

    private String paramKey;
    private String paramValue;
    private ParameterType paramType;
    private ParameterCategory category;
    private String description;
    private Boolean isActive;
    private Boolean isEditable;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonIgnore
    private String updatedBy;

    /**
     * Retorna o valor do parâmetro convertido para String.
     */
    @JsonIgnore
    public String getValueAsString() {
        return paramValue;
    }

    /**
     * Retorna o valor do parâmetro convertido para Integer.
     */
    @JsonIgnore
    public Integer getValueAsInteger() {
        if (paramValue == null) return null;
        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Retorna o valor do parâmetro convertido para Double.
     */
    @JsonIgnore
    public Double getValueAsDecimal() {
        if (paramValue == null) return null;
        try {
            return Double.parseDouble(paramValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Retorna o valor do parâmetro convertido para Boolean.
     */
    @JsonIgnore
    public Boolean getValueAsBoolean() {
        if (paramValue == null) return null;
        return Boolean.parseBoolean(paramValue) || "1".equals(paramValue);
    }
}

