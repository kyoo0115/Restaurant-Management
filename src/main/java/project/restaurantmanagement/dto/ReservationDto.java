package project.restaurantmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.model.Constants.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예약 정보 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

    private Long id;
    private Long customerId;
    private Long restaurantId;
    private Long managerId;
    private LocalDateTime reservationTime;
    private ReservationStatus status;

    public static ReservationDto from(ReservationEntity reservationEntity) {
        return ReservationDto.builder()
                .id(reservationEntity.getId())
                .customerId(reservationEntity.getCustomerEntity().getId())
                .restaurantId(reservationEntity.getRestaurantEntity().getId())
                .managerId(reservationEntity.getManagerEntity().getId())
                .reservationTime(reservationEntity.getReservationTime())
                .status(reservationEntity.getStatus())
                .build();
    }

    public static List<ReservationDto> from(List<ReservationEntity> reservationEntities) {
        return reservationEntities.stream().map(ReservationDto::from).toList();
    }
}
