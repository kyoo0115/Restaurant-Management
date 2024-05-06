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
import project.restaurantmanagement.exception.GlobalException;
import project.restaurantmanagement.repository.CustomerRepository;
import project.restaurantmanagement.repository.ReservationRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.security.TokenProvider;

import static project.restaurantmanagement.exception.ErrorCode.*;
import static project.restaurantmanagement.model.Type.ReservationStatus.*;

/**
 * 회원 관련 서비스를 제공하는 클래스입니다.
 * 회원의 등록, 인증, 리뷰 및 예약 관리 등을 수행합니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 이름(이메일)을 바탕으로 사용자 세부 정보를 로드합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByEmail(username)
                .orElseThrow(() -> new GlobalException(USER_NOT_EXIST));
    }

    /**
     * 신규 사용자 등록
     * 비밀번호 암호화 및 사용자 정보 저장
     */
    @Transactional
    public SignUpDto.Response register(SignUpDto.Request signUpRequest) {
        if (customerRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new GlobalException(EMAIL_ALREADY_EXIST);
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

    /**
     * 사용자 인증 처리
     * 이메일과 비밀번호를 통해 사용자를 인증하고 토큰 발행
     */
    @Transactional
    public SignInDto.Response authenticate(SignInDto.Request signInRequest) {
        CustomerEntity customer = customerRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new GlobalException(USER_NOT_EXIST));

        if (!passwordEncoder.matches(signInRequest.getPassword(), customer.getPassword())) {
            throw new GlobalException(WRONG_PASSWORD);
        }

        String token = tokenProvider.generateToken(customer.getId(), customer.getEmail(), customer.getUserType());

        return SignInDto.Response.builder()
                .email(customer.getEmail())
                .userType(customer.getUserType())
                .token(token)
                .build();
    }

    /**
     * 예약 생성
     * 사용자 인증 후 예약 정보 저장
     */
    @Transactional
    public ReservationDto createReservation(RegisterReservationDto request, String header) {
        log.info("Creating reservation at {}", request.getReservationTime());
        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new GlobalException(USER_NOT_EXIST));
        RestaurantEntity restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new GlobalException(RESTAURANT_NOT_EXIST));

        ReservationEntity reservationEntity = ReservationEntity.of(request, customer, restaurant);

        ReservationEntity savedEntity = reservationRepository.save(reservationEntity);

        return ReservationDto.from(savedEntity);
    }

    /**
     * 방문 확인 처리
     * 예약 정보와 방문자 정보가 일치해야만 방문 처리 가능
     */
    @Transactional
    public String visitRestaurant(VisitRestaurantDto request, Long reservationId, String header) {
        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new GlobalException(USER_NOT_EXIST));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GlobalException(RESERVATION_NOT_EXIST));

        // 방문자랑 예약자 확인
        checkVisitForm(customer, request);
        // 예약 승인 되었는지 확인
        checkReservation(reservation);

        reservation.setStatus(COMPLETED);
        reservation.setVisited(true);
        reservationRepository.save(reservation);

        return "예약 방문 처리가 완료되었습니다.";
    }

    /**
     * 예약 정보와 방문자가 입력한 정보 체크
     * (두 가지 다 일치해야 방문 확인 가능)
     */
    private void checkVisitForm(CustomerEntity customer, VisitRestaurantDto form) {
        if (!customer.getName().equals(form.getName()) ||
                !customer.getPhoneNumber().equals(form.getPhoneNumber())
        ) {
            throw new GlobalException(RESERVATION_WRONG_CUSTOMER);
        }
    }

    private void checkReservation(ReservationEntity reservation) {
        // 이미 취소된 예약인 경우
        if (reservation.getStatus() == CANCELLED) {
            throw new GlobalException(RESERVATION_ALREADY_CANCELED);
        }

        // 이미 완료 처리된 예약인 경우
        if (reservation.getStatus() == COMPLETED) {
            throw new GlobalException(RESERVATION_ALREADY_VISITED);
        }

        // 아직 승인이 된 예약이 아닌 경우
        if(reservation.getStatus() == PENDING) {
            throw new GlobalException(RESERVATION_NOT_PROCESSED);
        }
    }
}
