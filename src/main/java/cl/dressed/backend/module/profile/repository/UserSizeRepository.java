package cl.dressed.backend.module.profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import cl.dressed.backend.module.profile.entity.UserSize;

public interface UserSizeRepository extends JpaRepository<UserSize, Long> {
    List<UserSize> findByUserId(Long userId);
    Optional<UserSize> findByUserIdAndType(Long userId, String type);    

}
