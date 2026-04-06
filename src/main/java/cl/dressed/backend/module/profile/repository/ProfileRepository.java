package cl.dressed.backend.module.profile.repository;

import cl.dressed.backend.module.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
