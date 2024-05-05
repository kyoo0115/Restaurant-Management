package project.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.dto.*;
import project.restaurantmanagement.entity.CustomerEntity;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.entity.ReviewEntity;
import project.restaurantmanagement.exception.impl.AlreadyExistUserException;
import project.restaurantmanagement.exception.impl.IncorrectPasswordException;
import project.restaurantmanagement.model.Constants.ReservationStatus;
import project.restaurantmanagement.repository.CustomerRepository;
import project.restaurantmanagement.repository.ReservationRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.repository.ReviewRepository;
import project.restaurantmanagement.security.TokenProvider;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final TokenProvider tokenProvider;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "No such email -> " + username
                )
        );
    }

    @Transactional
    public SignUpDto.Response register(SignUpDto.Request signUpRequest) {
        if (customerRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AlreadyExistUserException();
        }

        log.info("register user -> {}", signUpRequest.getEmail());
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        log.info("password -> {}", signUpRequest.getPassword());

        CustomerEntity saved = customerRepository.save(CustomerEntity.from(signUpRequest));

        return SignUpDto.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }

    @Transactional
    public SignInDto.Response authenticate(SignInDto.Request signInRequest) {
        CustomerEntity customer = customerRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + signInRequest.getEmail()));

        if (!passwordEncoder.matches(signInRequest.getPassword(), customer.getPassword())) {
            throw new IncorrectPasswordException();
        }

        String token = tokenProvider.generateToken(customer.getId(), customer.getEmail(), customer.getUserType());

        return SignInDto.Response.builder()
                .email(customer.getEmail())
                .userType(customer.getUserType())
                .token(token)
                .build();
    }

    @Transactional
    public List<RestaurantDto> viewRestaurants() {
        log.info("view restaurants");
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
        return RestaurantDto.from(restaurantEntities);
    }

    @Transactional
    public ReservationDto createReservation(RegisterReservationDto request, String header) {
        log.info("Creating reservation at {}", request.getReservationTime());
        String token = tokenProvider.getToken(header);
        long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        RestaurantEntity restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        ReservationEntity reservationEntity = ReservationEntity.of(request, customer, restaurant);

        ReservationEntity savedEntity = reservationRepository.save(reservationEntity);

        return ReservationDto.from(savedEntity);
    }

    @Transactional
    public void visitedRestaurant(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setVisited(true);
        reservationRepository.save(reservation);
    }



//    @Transactional
//    public void addReview(ReviewDto request, Long restaurantId) {
//        log.info("add review -> {}", request.getComments());
//        reviewRepository.save(ReviewEntity)
//    }
}
