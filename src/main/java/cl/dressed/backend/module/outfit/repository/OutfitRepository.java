package cl.dressed.backend.module.outfit.repository;

import cl.dressed.backend.module.outfit.entity.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {

    // Outfits pre-generados por el modelo ML (user_id IS NULL)
    // donde el top contiene la talla del usuario y el bottom también
    @Query("""
        SELECT DISTINCT o FROM Outfit o
        JOIN OutfitGarment og1 ON og1.outfitId = o.id AND og1.role = 'top'
        JOIN OutfitGarment og2 ON og2.outfitId = o.id AND og2.role = 'bottom'
        JOIN Garment t ON t.id = og1.garmentId
        JOIN Garment b ON b.id = og2.garmentId
        WHERE o.userId IS NULL
          AND t.size LIKE CONCAT('%', :topSize, '%')
          AND b.size LIKE CONCAT('%', :bottomSize, '%')
        ORDER BY o.id DESC
        LIMIT 100
        """)
    List<Outfit> findCompatibleOutfits(
        @Param("topSize") String topSize,
        @Param("bottomSize") String bottomSize
    );
}