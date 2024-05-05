package project.restaurantmanagement.dto;

import lombok.*;

/**
 * 매장 정보 등록 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRestaurantDto {

    private String restaurantName;
    private String location;
    private String description;
    private String phoneNumber;
}
