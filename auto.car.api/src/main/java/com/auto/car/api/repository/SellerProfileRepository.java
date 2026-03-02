package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.SellerProfileEntity;
import com.auto.car.api.enums.SellerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfileEntity, String> {

    Optional<SellerProfileEntity> findByUserId(String userId);

    Optional<SellerProfileEntity> findByCompanyId(String companyId);

    List<SellerProfileEntity> findBySellerType(SellerType sellerType);

    boolean existsByUserId(String userId);

    boolean existsByCompanyId(String companyId);
}
