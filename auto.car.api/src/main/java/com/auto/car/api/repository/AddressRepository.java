package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.AddressEntity;
import com.auto.car.api.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, String> {

    List<AddressEntity> findByUserIdAndOwnerType(String userId, OwnerType ownerType);

    List<AddressEntity> findByCompanyIdAndOwnerType(String companyId, OwnerType ownerType);

    List<AddressEntity> findByUserId(String userId);

    List<AddressEntity> findByCompanyId(String companyId);

    List<AddressEntity> findByCity(String city);

    List<AddressEntity> findByState(String state);
}

