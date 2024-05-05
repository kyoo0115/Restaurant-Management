package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import project.restaurantmanagement.dto.RegisterReservationDto;
import project.restaurantmanagement.model.Constants.ReservationStatus;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

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

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public static ReservationEntity of(RegisterReservationDto request, CustomerEntity customer, RestaurantEntity restaurant) {
        return ReservationEntity.builder()
                .customerEntity(customer)
                .managerEntity(restaurant.getManagerEntity())
                .restaurantEntity(restaurant)
                .peopleCount(request.getPeopleCount())
                .reservationTime(request.getReservationTime())
                .status(ReservationStatus.PENDING)
                .build();
    }
}