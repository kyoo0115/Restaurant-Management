package project.restaurantmanagement.repository;

import org.springframework.stereotype.Repository;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.entity.ReviewEntity;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<ReviewEntity, Long> {


    List<ReviewEntity> findReviewEntitiesByRestaurantEntity(RestaurantEntity restaurant);
}
