package cl.dressed.backend.module.admin.repository;

import cl.dressed.backend.module.admin.entity.ScrapingRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapingRunRepository extends JpaRepository<ScrapingRunEntity, Integer> {
    
    /**
     * Obtiene los últimos scraping runs de una tienda
     */
    List<ScrapingRunEntity> findByStoreIdOrderByStartedAtDesc(Integer storeId);
    
    /**
     * Obtiene todos los runs en estado RUNNING
     */
    List<ScrapingRunEntity> findByStatus(String status);
    
    /**
     * Obtiene runs de una tienda con un tipo específico
     */
    List<ScrapingRunEntity> findByStoreIdAndType(Integer storeId, String type);
    
    /**
     * Obtiene el último run exitoso de una tienda
     */
    Optional<ScrapingRunEntity> findFirstByStoreIdAndStatusOrderByFinishedAtDesc(
        Integer storeId,
        String status
    );
    
    /**
     * Obtiene runs dentro de un rango de fechas
     */
    List<ScrapingRunEntity> findByStartedAtBetweenOrderByStartedAtDesc(
        LocalDateTime start,
        LocalDateTime end
    );
}