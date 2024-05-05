package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import project.restaurantmanagement.dto.ReviewDto;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "review")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    private String title;
    private String comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "manager_id")
    private ManagerEntity managerEntity;

    private double rating;

    public static ReviewEntity of(ReviewDto reviewDto, CustomerEntity customerEntity, ReservationEntity reservationEntity) {

        return ReviewEntity.builder()
                .title(reviewDto.getTitle())
                .comment(reviewDto.getComments())
                .customerEntity(customerEntity)
                .restaurantEntity(reservationEntity.getRestaurantEntity())
                .managerEntity(reservationEntity.getManagerEntity())
                .rating(reviewDto.getRating().getRateValue())
                .build();
    }
}
