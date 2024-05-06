package project.restaurantmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
