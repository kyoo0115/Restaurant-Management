package project.restaurantmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.restaurantmanagement.entity.RestaurantEntity;

import java.util.List;

/**
 * 매장 정보 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {

    private Long managerId;
    private String name;
    private String description;
    private String phoneNumber;

    public static RestaurantDto from(RestaurantEntity restaurant) {
        return RestaurantDto.builder()
                .managerId(restaurant.getManagerEntity().getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .phoneNumber(restaurant.getPhoneNumber())
                .build();
    }

    public static List<RestaurantDto> from(List<RestaurantEntity> restaurantEntities) {
        return restaurantEntities.stream().map(RestaurantDto::from).toList();
    }
}
