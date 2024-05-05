package project.restaurantmanagement.dto;

import lombok.*;

/**
 * 고객 방문 확인을 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRestaurantDto {
    private String name;
    private String phoneNumber;
}
