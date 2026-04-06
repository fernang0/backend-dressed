package cl.dressed.backend.module.admin.repository;

import cl.dressed.backend.module.admin.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
}
