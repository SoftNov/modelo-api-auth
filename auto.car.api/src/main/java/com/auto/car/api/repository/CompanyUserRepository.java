package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.CompanyUserEntity;
import com.auto.car.api.enums.CompanyUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUserEntity, CompanyUserEntity.CompanyUserId> {

    List<CompanyUserEntity> findByCompanyId(String companyId);

    List<CompanyUserEntity> findByUserId(String userId);

    Optional<CompanyUserEntity> findByCompanyIdAndUserId(String companyId, String userId);

    List<CompanyUserEntity> findByCompanyIdAndRole(String companyId, CompanyUserRole role);

    boolean existsByCompanyIdAndUserId(String companyId, String userId);
}

