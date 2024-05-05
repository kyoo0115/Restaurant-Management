package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import project.restaurantmanagement.dto.RegisterRestaurantDto;

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
    private double rating;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;

}
