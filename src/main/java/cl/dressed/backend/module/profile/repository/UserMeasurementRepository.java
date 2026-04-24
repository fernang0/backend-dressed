package cl.dressed.backend.module.profile.repository;

import cl.dressed.backend.module.profile.entity.UserMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserMeasurementRepository extends JpaRepository<UserMeasurement, Long> {
    Optional<UserMeasurement> findByUserId(Long userId);
}