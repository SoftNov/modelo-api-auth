package com.auto.car.api.repository.entity;

import com.auto.car.api.enums.CompanyUserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "company_user")
@IdClass(CompanyUserEntity.CompanyUserId.class)
@Getter
@Setter
@NoArgsConstructor
public class CompanyUserEntity {

    @Id
    @Column(name = "company_id", length = 36, nullable = false)
    private String companyId;

    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private CompanyEntity company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30, nullable = false)
    private CompanyUserRole role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Composite Primary Key Class
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CompanyUserId implements Serializable {
        private String companyId;
        private String userId;

        public CompanyUserId(String companyId, String userId) {
            this.companyId = companyId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompanyUserId that = (CompanyUserId) o;
            return Objects.equals(companyId, that.companyId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(companyId, userId);
        }
    }
}
