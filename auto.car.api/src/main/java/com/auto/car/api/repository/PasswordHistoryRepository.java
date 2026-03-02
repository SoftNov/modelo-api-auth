package com.auto.car.api.repository;

import com.auto.car.api.repository.entity.PasswordHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistoryEntity, String> {

    /**
     * Busca as últimas N senhas do usuário ordenadas por data de criação (mais recente primeiro).
     */
    @Query("SELECT ph FROM PasswordHistoryEntity ph WHERE ph.user.id = :userId ORDER BY ph.createdAt DESC LIMIT :limit")
    List<PasswordHistoryEntity> findLastPasswordsByUserId(@Param("userId") String userId, @Param("limit") int limit);

    /**
     * Conta o total de senhas no histórico do usuário.
     */
    long countByUserId(String userId);

    /**
     * Remove as senhas mais antigas do usuário, mantendo apenas as últimas N.
     */
    @Query("DELETE FROM PasswordHistoryEntity ph WHERE ph.user.id = :userId AND ph.id NOT IN " +
           "(SELECT ph2.id FROM PasswordHistoryEntity ph2 WHERE ph2.user.id = :userId ORDER BY ph2.createdAt DESC LIMIT :keepCount)")
    void deleteOldPasswords(@Param("userId") String userId, @Param("keepCount") int keepCount);
}

