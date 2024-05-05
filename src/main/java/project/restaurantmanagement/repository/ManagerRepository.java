package project.restaurantmanagement.repository;

import org.springframework.stereotype.Repository;
import project.restaurantmanagement.entity.ManagerEntity;

import java.util.Optional;

@Repository
public interface ManagerRepository extends BaseRepository<ManagerEntity, Long> {
    Optional<ManagerEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
