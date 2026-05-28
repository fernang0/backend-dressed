package cl.dressed.backend.module.auth.repository;

import cl.dressed.backend.module.auth.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<String> findRolesByUserId(@Param("userId") Long userId);

    long countByActiveTrue();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since")
    long countUsersCreatedSince(@Param("since") LocalDateTime since);

    @Query("SELECT u FROM User u WHERE u.updatedAt < :since")
    List<User> findInactiveUsersSince(@Param("since") LocalDateTime since);

    @Query(value = """
        SELECT DATE(u.created_at) AS created_date, COUNT(*) AS user_count
        FROM users u
        WHERE u.created_at >= :since
        GROUP BY DATE(u.created_at)
        ORDER BY DATE(u.created_at)
        """, nativeQuery = true)
    List<Object[]> countUsersCreatedPerDaySince(@Param("since") LocalDateTime since);

    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
