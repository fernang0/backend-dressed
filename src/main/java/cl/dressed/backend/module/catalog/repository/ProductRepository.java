package cl.dressed.backend.module.catalog.repository;

import cl.dressed.backend.module.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
