package cl.dressed.backend.module.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.dressed.backend.module.auth.entity.PasswordRecovery;
import cl.dressed.backend.module.auth.entity.User;

import java.util.Optional;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Long> {

    Optional<PasswordRecovery> findByToken(String token);

    @Modifying
    @Query("UPDATE PasswordRecovery p SET p.used = true WHERE p.user = :user AND p.used = false")
    void invalidatePreviousTokens(@Param("user") User user);
}