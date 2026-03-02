package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.EmailConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationTokenEntity, String> {

    Optional<EmailConfirmationTokenEntity> findByToken(String token);

    Optional<EmailConfirmationTokenEntity> findByUserIdAndUsedAtIsNull(String userId);

    @Query("SELECT t FROM EmailConfirmationTokenEntity t WHERE t.user.id = :userId ORDER BY t.createdAt DESC LIMIT 1")
    Optional<EmailConfirmationTokenEntity> findLatestByUserId(@Param("userId") String userId);
}
