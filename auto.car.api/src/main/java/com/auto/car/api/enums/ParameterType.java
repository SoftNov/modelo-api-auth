package com.auto.car.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum que define os tipos de dados dos parâmetros do sistema.
 */
@Getter
@AllArgsConstructor
public enum ParameterType {

    STRING("STRING", "Texto"),
    INTEGER("INTEGER", "Número inteiro"),
    DECIMAL("DECIMAL", "Número decimal"),
    BOOLEAN("BOOLEAN", "Verdadeiro/Falso"),
    JSON("JSON", "Objeto JSON");

    private final String value;
    private final String description;

    public static ParameterType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ParameterType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}

