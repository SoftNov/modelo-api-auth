package com.auto.car.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum que define as categorias dos parâmetros do sistema.
 */
@Getter
@AllArgsConstructor
public enum ParameterCategory {

    SYSTEM("SYSTEM", "Sistema", "Configurações gerais do sistema"),
    BUSINESS("BUSINESS", "Negócio", "Regras e configurações de negócio"),
    SECURITY("SECURITY", "Segurança", "Configurações de segurança"),
    EMAIL("EMAIL", "E-mail", "Configurações de envio de e-mail"),
    NOTIFICATION("NOTIFICATION", "Notificação", "Configurações de notificações");

    private final String value;
    private final String displayName;
    private final String description;

    public static ParameterCategory fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ParameterCategory category : values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }
}

