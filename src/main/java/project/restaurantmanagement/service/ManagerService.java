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
import project.restaurantmanagement.entity.ManagerEntity;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.entity.ReviewEntity;
import project.restaurantmanagement.exception.GlobalException;
import project.restaurantmanagement.model.Type.AcceptStatus;
import project.restaurantmanagement.repository.ManagerRepository;
import project.restaurantmanagement.repository.ReservationRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.repository.ReviewRepository;
import project.restaurantmanagement.security.TokenProvider;

import java.util.List;
import java.util.Objects;

import static project.restaurantmanagement.exception.ErrorCode.*;
import static project.restaurantmanagement.model.Type.ReservationStatus.*;

/**
 * 매니저 관련 서비스를 제공하는 클래스입니다.
 * 매니저의 등록, 인증, 매장 및 예약 관리 등을 수행합니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService implements UserDetailsService {

    private final ManagerRepository managerRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 사용자 이름(이메일)을 바탕으로 사용자 세부 정보를 로드합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return managerRepository.findByEmail(username).orElseThrow(
                () -> new GlobalException(USER_NOT_EXIST));
    }

    /**
     * 신규 매니저 등록
     * 비밀번호는 암호화하여 저장합니다.
     */
    @Transactional
    public SignUpDto.Response register(SignUpDto.Request signUpRequest) {
        if (managerRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new GlobalException(USER_NOT_EXIST);
        }

        log.info("register user -> {}", signUpRequest.getEmail());
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        log.info("password -> {}", signUpRequest.getPassword());

        ManagerEntity saved = managerRepository.save(ManagerEntity.from(signUpRequest));

        return SignUpDto.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }

    /**
     * 매니저 로그인 및 토큰 발행
     */
    @Transactional
    public SignInDto.Response authenticate(SignInDto.Request signInRequest) {
        ManagerEntity managerEntity = managerRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + signInRequest.getEmail()));

        if (!passwordEncoder.matches(signInRequest.getPassword(), managerEntity.getPassword())) {
            throw new GlobalException(WRONG_PASSWORD);
        }

        String token = tokenProvider.generateToken(managerEntity.getId(), managerEntity.getEmail(), managerEntity.getUserType());

        return SignInDto.Response.builder()
                .email(managerEntity.getEmail())
                .userType(managerEntity.getUserType())
                .token(token)
                .build();
    }

    /**
     * 매니저가 관리하는 매장 생성
     */
    @Transactional
    public RestaurantDto createRestaurant(RegisterRestaurantDto registerRestaurantDto, String header) {

        String token = tokenProvider.getToken(header);
        String email = tokenProvider.getUsername(token);

        ManagerEntity manager = managerRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        RestaurantEntity savedRestaurant = restaurantRepository.save(RestaurantEntity.of(registerRestaurantDto, manager));

        return RestaurantDto.from(savedRestaurant);
    }

    /**
     * 매니저 별 예약 정보 조회
     */
    @Transactional
    public List<ReservationDto> viewReservations(String header, Long restaurantId) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new GlobalException(RESTAURANT_NOT_EXIST));

        if (!manager.getRestaurants().contains(restaurant)) {
            throw new GlobalException(SHOP_MANAGER_NOT_EXIST);
        }

        return ReservationDto.from(reservationRepository.findReservationEntitiesByManagerEntity(manager));
    }

    /**
     * 예약 정보를 이용하여 예약 승인/거절 결정
     */
    @Transactional
    public String acceptOrRefuseReservation(String header, Long reservationId, AcceptStatus acceptStatus) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GlobalException(RESERVATION_NOT_EXIST));

        checkReservation(reservation);

        reservation.setStatus(acceptStatus.getStatus() ? ACCEPTED : CANCELLED);

        reservationRepository.save(reservation);

        return "예약 번호 " + reservationId + "에 대한 " + (acceptStatus.getStatus() ? "승인" : "거절") + " 처리가 완료되었습니다.";
    }

    /**
     * 리뷰 조회
     */
    @Transactional
    public List<ReviewDto> viewReviews(String header, Long restaurantId) {
        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new GlobalException(RESTAURANT_NOT_EXIST));

        if (!manager.getRestaurants().contains(restaurant)) {
            throw new GlobalException(SHOP_MANAGER_NOT_EXIST);
        }

        return ReviewDto.from(reviewRepository.findReviewEntitiesByRestaurantEntity(restaurant));
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public String deleteReview(String header, Long reviewId) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GlobalException(REVIEW_NOT_EXIST));

        if (!Objects.equals(review.getManagerEntity().getId(), manager.getId())) {
            throw new GlobalException(REVIEW_NOT_YOURS);
        }

        reviewRepository.deleteById(reviewId);
        return "리뷰가 성공적으로 삭제되었습니다. [" + review.getRestaurantEntity().getName() + "]";
    }

    /**
     * 예약 검증
     */
    private void checkReservation(ReservationEntity reservation) {
        // 이미 취소된 예약인 경우
        if (reservation.getStatus() == CANCELLED) {
            throw new GlobalException(RESERVATION_ALREADY_CANCELED);
        }

        // 이미 완료 처리된 예약인 경우
        if (reservation.getStatus() == COMPLETED) {
            throw new GlobalException(RESERVATION_ALREADY_VISITED);
        }

        // 이미 확인(승인)한 예약인 경우
        if (reservation.getStatus() == ACCEPTED) {
            throw new GlobalException(RESERVATION_ALREADY_PROCESSED);
        }
    }
}
