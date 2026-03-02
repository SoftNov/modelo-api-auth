package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, String> {

    Optional<PersonEntity> findByUserId(String userId);

    Optional<PersonEntity> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByUserId(String userId);
}

