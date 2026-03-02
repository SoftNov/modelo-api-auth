package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.CompanyEntity;
import com.auto.car.api.enums.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {

    Optional<CompanyEntity> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    List<CompanyEntity> findByStatus(CompanyStatus status);
}

