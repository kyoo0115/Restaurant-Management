package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import project.restaurantmanagement.dto.RegisterReservationDto;
import project.restaurantmanagement.model.type.ReservationStatus;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 예약 entity
 * 정보 : 인원, 예약 시간, 식당 id
 */

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private ManagerEntity managerEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;

    private Integer peopleCount;
    private LocalDateTime reservationTime;
    private boolean visited;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public static ReservationEntity of(RegisterReservationDto request, CustomerEntity customer, RestaurantEntity restaurant) {
        return ReservationEntity.builder()
                .customerEntity(customer)
                .managerEntity(restaurant.getManagerEntity())
                .restaurantEntity(restaurant)
                .peopleCount(request.getPeopleCount())
                .reservationTime(request.getReservationTime())
                .visited(false)
                .status(ReservationStatus.PENDING)
                .build();
    }
}
