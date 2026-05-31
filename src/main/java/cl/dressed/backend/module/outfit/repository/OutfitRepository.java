package cl.dressed.backend.module.outfit.repository;

import cl.dressed.backend.module.outfit.entity.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByUserIdOrderByGeneratedAtDesc(Long userId);
}