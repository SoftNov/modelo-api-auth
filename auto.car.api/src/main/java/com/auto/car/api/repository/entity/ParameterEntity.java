package com.auto.car.api.repository.entity;

import com.auto.car.api.enums.ParameterCategory;
import com.auto.car.api.enums.ParameterType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade que representa os parâmetros e configurações do sistema.
 */
@Entity
@Table(name = "parameter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "param_key", length = 100, nullable = false, unique = true)
    private String paramKey;

    @Column(name = "param_value", columnDefinition = "TEXT", nullable = false)
    private String paramValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_type", length = 30, nullable = false)
    private ParameterType paramType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    private ParameterCategory category;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_editable")
    private Boolean isEditable;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isEditable == null) {
            this.isEditable = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

