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
import project.restaurantmanagement.exception.ReservationServiceException;
import project.restaurantmanagement.exception.impl.AlreadyExistUserException;
import project.restaurantmanagement.exception.impl.IncorrectPasswordException;
import project.restaurantmanagement.model.Type.ReservationStatus;
import project.restaurantmanagement.repository.CustomerRepository;
import project.restaurantmanagement.repository.ReservationRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.repository.ReviewRepository;
import project.restaurantmanagement.security.TokenProvider;

import java.util.List;
import java.util.Objects;

import static project.restaurantmanagement.exception.ErrorCode.*;
import static project.restaurantmanagement.model.Type.ReservationStatus.COMPLETED;

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
        return customerRepository.findByEmail(username)
                .orElseThrow(() -> new ReservationServiceException(USER_NOT_EXIST));
    }

    @Transactional
    public SignUpDto.Response register(SignUpDto.Request signUpRequest) {
        if (customerRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ReservationServiceException(EMAIL_ALREADY_EXIST);
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
                .orElseThrow(() -> new ReservationServiceException(USER_NOT_EXIST));

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
                .orElseThrow(() -> new ReservationServiceException(USER_NOT_EXIST));
        RestaurantEntity restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ReservationServiceException(RESTAURANT_NOT_EXIST));

        ReservationEntity reservationEntity = ReservationEntity.of(request, customer, restaurant);

        ReservationEntity savedEntity = reservationRepository.save(reservationEntity);

        return ReservationDto.from(savedEntity);
    }

    @Transactional
    public String visitRestaurant(VisitRestaurantDto request, Long reservationId, String header) {
        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ReservationServiceException(USER_NOT_EXIST));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationServiceException(RESERVATION_NOT_EXIST));

        // 방문자랑 예약자 확인
        checkVisitForm(customer, request);
        // 예약 승인 되었는지 확인
        checkReservation(reservation);

        reservation.setStatus(COMPLETED);
        reservation.setVisited(true);
        reservationRepository.save(reservation);

        return "예약 방문 처리가 완료되었습니다.";
    }

    @Transactional
    public String addReview(ReviewDto request, Long reservationId, String header) {
        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ReservationServiceException(RESERVATION_CUSTOMER_NOT_EXIST));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationServiceException(RESERVATION_NOT_EXIST));

        // 리뷰 작성은 예약이 완료된 후에만 가능
        if(!Objects.equals(reservation.getCustomerEntity().getId(), customer.getId())) {
            throw new ReservationServiceException(REVIEW_NOT_YOURS);
        }

        if(reservation.getStatus() != COMPLETED) {
            throw new ReservationServiceException(RESERVATION_NOT_PROCESSED);
        }

        log.info("add review -> {}", request.getComments());
        reviewRepository.save(ReviewEntity.of(request, customer, reservation));
        return "해당 식당 " + "[" + reservation.getRestaurantEntity().getName() + "]" + "의 리뷰 남겼습니다";
    }

    /**
     * 예약 정보와 방문자가 입력한 정보 체크
     * <p>
     * (두 가지 다 일치해야 방문 확인 가능)
     */
    private void checkVisitForm(CustomerEntity customer, VisitRestaurantDto form) {
        if (!customer.getName().equals(form.getName()) ||
                !customer.getPhoneNumber().equals(form.getPhoneNumber())
        ) {
            throw new ReservationServiceException(WRONG_VISIT_FORM);
        }
    }

    private void checkReservation(ReservationEntity reservation) {
        // 이미 취소된 예약인 경우
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationServiceException(RESERVATION_ALREADY_CANCELED);
        }

        // 이미 완료 처리된 예약인 경우
        if (reservation.getStatus() == COMPLETED) {
            throw new ReservationServiceException(RESERVATION_ALREADY_VISITED);
        }

        // 아직 승인이 된 예약이 아닌 경우
        if(reservation.getStatus() == ReservationStatus.PENDING) {
            throw new ReservationServiceException(RESERVATION_NOT_PROCESSED);
        }
    }
}
