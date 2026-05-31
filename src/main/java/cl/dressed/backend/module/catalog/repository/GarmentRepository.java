package cl.dressed.backend.module.catalog.repository;

import cl.dressed.backend.module.catalog.entity.Garment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GarmentRepository extends JpaRepository<Garment, Integer> {
    Page<Garment> findByCategory(String category, Pageable pageable);
    Page<Garment> findBySizeContaining(String size, Pageable pageable);
    Page<Garment> findByInStock(Boolean inStock, Pageable pageable);
    boolean existsByDedupHash(String dedupHash);

    // Para el generador de outfits: busca prendas por categoría, talla y stock
    List<Garment> findByCategoryAndSizeContainingAndInStock(
        String category, String size, Boolean inStock);
}