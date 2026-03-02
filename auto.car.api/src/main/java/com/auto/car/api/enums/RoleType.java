package com.auto.car.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum que define os papéis (roles) disponíveis no sistema.
 * Utilizado para controle de acesso e permissões.
 */
@Getter
@AllArgsConstructor
public enum RoleType {

    ADMIN("ADMIN", "Administrador", "Acesso total ao sistema"),
    USER("USER", "Usuário", "Usuário padrão do sistema"),
    SELLER("SELLER", "Vendedor", "Usuário que pode anunciar e vender veículos"),
    BUYER("BUYER", "Comprador", "Usuário que pode comprar veículos"),
    MANAGER("MANAGER", "Gerente", "Gerente com permissões administrativas limitadas"),
    SUPPORT("SUPPORT", "Suporte", "Equipe de suporte ao cliente");

    /**
     * Nome da role no formato Spring Security (ROLE_*)
     */
    private final String roleName;

    /**
     * Nome amigável para exibição
     */
    private final String displayName;

    /**
     * Descrição da role
     */
    private final String description;

    /**
     * Busca uma role pelo nome.
     *
     * @param roleName nome da role (ex: "ROLE_ADMIN" ou "ADMIN")
     * @return RoleType correspondente ou null se não encontrar
     */
    public static RoleType fromRoleName(String roleName) {
        if (roleName == null) {
            return null;
        }

        String normalizedName = roleName.toUpperCase();
        if (!normalizedName.startsWith("ROLE_")) {
            normalizedName = "ROLE_" + normalizedName;
        }

        for (RoleType role : values()) {
            if (role.getRoleName().equals(normalizedName)) {
                return role;
            }
        }
        return null;
    }

    /**
     * Verifica se a role é administrativa (ADMIN ou MANAGER).
     *
     * @return true se for role administrativa
     */
    public boolean isAdministrative() {
        return this == ADMIN || this == MANAGER;
    }

    /**
     * Verifica se a role pode vender veículos.
     *
     * @return true se pode vender
     */
    public boolean canSell() {
        return this == ADMIN || this == SELLER || this == MANAGER;
    }

    /**
     * Verifica se a role pode comprar veículos.
     *
     * @return true se pode comprar
     */
    public boolean canBuy() {
        return this == ADMIN || this == BUYER || this == USER || this == MANAGER;
    }
}

