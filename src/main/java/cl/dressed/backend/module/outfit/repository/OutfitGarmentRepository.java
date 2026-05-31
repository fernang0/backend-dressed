package cl.dressed.backend.module.outfit.repository;

import cl.dressed.backend.module.outfit.entity.OutfitGarment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutfitGarmentRepository extends JpaRepository<OutfitGarment, Long> {
    List<OutfitGarment> findByOutfitId(Long outfitId);
}