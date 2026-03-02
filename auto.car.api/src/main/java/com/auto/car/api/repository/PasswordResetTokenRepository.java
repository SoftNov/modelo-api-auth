package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, String> {

    @Query("SELECT t FROM PasswordResetTokenEntity t WHERE t.user.id = :userId AND t.code = :code AND t.usedAt IS NULL ORDER BY t.createdAt DESC LIMIT 1")
    Optional<PasswordResetTokenEntity> findValidToken(@Param("userId") String userId, @Param("code") String code);

    @Query("SELECT t FROM PasswordResetTokenEntity t WHERE t.user.username = :username AND t.code = :code AND t.usedAt IS NULL ORDER BY t.createdAt DESC LIMIT 1")
    Optional<PasswordResetTokenEntity> findValidTokenByUsername(@Param("username") String username, @Param("code") String code);

    @Query("SELECT t FROM PasswordResetTokenEntity t WHERE t.user.username = :username AND t.usedAt IS NULL AND t.validatedAt IS NOT NULL ORDER BY t.createdAt DESC LIMIT 1")
    Optional<PasswordResetTokenEntity> findValidatedTokenByUsername(@Param("username") String username);

    @Query("SELECT COUNT(t) FROM PasswordResetTokenEntity t WHERE t.user.username = :username AND t.createdAt > :since")
    long countRecentRequestsByUsername(@Param("username") String username, @Param("since") java.time.LocalDateTime since);
}

