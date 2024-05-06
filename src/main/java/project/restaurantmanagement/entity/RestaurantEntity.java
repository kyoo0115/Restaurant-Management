package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.restaurantmanagement.dto.RegisterRestaurantDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 식당 정보를 저장하는 엔티티 클래스입니다.
 * 매장 이름, 위치, 설명, 전화번호 및 관리자 정보를 포함합니다.
 * 이 엔티티는 예약 및 리뷰와 관계가 설정되어 있습니다.
 */

@Entity
@Table(name = "restaurant")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id", nullable = false)
    private Long id;

    private String name;
    private String location;
    private String description;
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private ManagerEntity managerEntity;

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.ALL)
    private List<ReviewEntity> reviewEntities = new ArrayList<>();

    public static RestaurantEntity of(RegisterRestaurantDto registerRestaurantDto, ManagerEntity ManagerEntity) {

        return RestaurantEntity.builder()
                .name(registerRestaurantDto.getRestaurantName())
                .location(registerRestaurantDto.getLocation())
                .description(registerRestaurantDto.getDescription())
                .phoneNumber(registerRestaurantDto.getPhoneNumber())
                .managerEntity(ManagerEntity)
                .build();
    }
}
