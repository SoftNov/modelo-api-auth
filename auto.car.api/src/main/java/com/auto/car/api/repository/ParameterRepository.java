package com.auto.car.api.repository;

import com.auto.car.api.enums.ParameterCategory;
import com.auto.car.api.repository.entity.ParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<ParameterEntity, String> {

    Optional<ParameterEntity> findByParamKey(String paramKey);

    Optional<ParameterEntity> findByParamKeyAndIsActiveTrue(String paramKey);

    List<ParameterEntity> findByCategory(ParameterCategory category);

    List<ParameterEntity> findByCategoryAndIsActiveTrue(ParameterCategory category);

    List<ParameterEntity> findByIsActiveTrue();

    List<ParameterEntity> findByIsEditableTrue();

    boolean existsByParamKey(String paramKey);
}

