package cl.dressed.backend.module.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.dressed.backend.module.profile.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

}