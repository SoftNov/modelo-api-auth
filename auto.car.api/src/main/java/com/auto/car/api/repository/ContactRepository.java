package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.ContactEntity;
import com.auto.car.api.enums.ContactType;
import com.auto.car.api.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, String> {

    List<ContactEntity> findByUserIdAndOwnerType(String userId, OwnerType ownerType);

    List<ContactEntity> findByCompanyIdAndOwnerType(String companyId, OwnerType ownerType);

    List<ContactEntity> findByUserId(String userId);

    List<ContactEntity> findByCompanyId(String companyId);

    Optional<ContactEntity> findByUserIdAndIsPrimaryTrue(String userId);

    Optional<ContactEntity> findByCompanyIdAndIsPrimaryTrue(String companyId);

    List<ContactEntity> findByUserIdAndContactType(String userId, ContactType contactType);

    List<ContactEntity> findByCompanyIdAndContactType(String companyId, ContactType contactType);
}

