package project.restaurantmanagement.repository;

import org.springframework.stereotype.Repository;
import project.restaurantmanagement.entity.RestaurantEntity;

import java.util.List;

@Repository
public interface RestaurantRepository extends BaseRepository<RestaurantEntity, Long> {
    List<RestaurantEntity> findAll();
}
