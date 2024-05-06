package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import project.restaurantmanagement.dto.ReviewDto;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 리뷰 정보를 저장하는 엔티티 클래스입니다.
 * 리뷰 제목, 코멘트, 평점 및 관련 엔티티 매핑(고객, 식당, 매니저)을 포함합니다.
 */

@Entity
@Table(name = "review")
@Getter
@Setter
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
