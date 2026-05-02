package cl.dressed.backend.module.profile.repository;

import cl.dressed.backend.module.profile.entity.UserStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStyleRepository extends JpaRepository<UserStyle, Integer> {

    List<UserStyle> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}