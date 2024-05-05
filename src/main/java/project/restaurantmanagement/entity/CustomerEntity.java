package project.restaurantmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.restaurantmanagement.dto.SignUpDto;
import project.restaurantmanagement.model.Type.UserType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static project.restaurantmanagement.model.Type.UserType.CUSTOMER;

/**
 * 고객 entity
 * 정보 : 이름, 이메일(로그인 아이디), 패스워드, 전화번호
 */

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity implements UserDetails {

    @Id
    @Column(name = "customer_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;
    private String name;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "customerEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationEntity> reservationEntities;

    public static CustomerEntity from(SignUpDto.Request request) {

        return CustomerEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .userType(CUSTOMER)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
