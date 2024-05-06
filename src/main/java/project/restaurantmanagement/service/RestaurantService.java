package project.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.dto.RestaurantDto;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.exception.GlobalException;
import project.restaurantmanagement.repository.RestaurantRepository;

import java.util.List;

import static project.restaurantmanagement.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    /**
     * 매장 목록 조회
     * 모든 매장 정보를 조회하여 반환
     */
    public List<RestaurantDto> viewRestaurants() {
        log.info("view restaurants");
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
        return RestaurantDto.from(restaurantEntities);
    }

    /**
     * 해당 매장 조회
     * 해당 매장 정보를 조회하여 반환
     */
    public RestaurantDto viewRestaurant(Long restaurantId) {
        log.info("view restaurant -> {}", restaurantId);
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new GlobalException(RESTAURANT_NOT_EXIST));
        return RestaurantDto.from(restaurant);
    }
}
