package project.restaurantmanagement.repository;

import org.springframework.stereotype.Repository;
import project.restaurantmanagement.dto.RestaurantDto;
import project.restaurantmanagement.entity.CustomerEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseRepository<CustomerEntity, Long> {
    boolean existsByEmail(String email);

    Optional<CustomerEntity> findByEmail(String username);

//    Object findByEmail(String username);
}
