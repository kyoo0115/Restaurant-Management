package project.restaurantmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 예약 정보 등록 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterReservationDto {

    private Integer peopleCount;
    private Long restaurantId;
    private LocalDateTime reservationTime;
}
