package project.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.dto.ReviewDto;
import project.restaurantmanagement.entity.*;
import project.restaurantmanagement.exception.GlobalException;
import project.restaurantmanagement.repository.*;
import project.restaurantmanagement.security.TokenProvider;

import java.util.List;
import java.util.Objects;

import static project.restaurantmanagement.exception.ErrorCode.*;
import static project.restaurantmanagement.model.Type.ReservationStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ManagerRepository managerRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;
    private final TokenProvider tokenProvider;


    /**
     * 리뷰 추가
     * 예약이 완료된 후에만 리뷰 작성 가능
     */
    @Transactional
    public String addReview(ReviewDto request, Long reservationId, String header) {
        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new GlobalException(CUSTOMER_NOT_EXIST));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GlobalException(RESERVATION_NOT_EXIST));

        // 리뷰 작성은 예약이 완료된 후에만 가능
        if(!Objects.equals(reservation.getCustomerEntity().getId(), customer.getId())) {
            throw new GlobalException(REVIEW_NOT_YOURS);
        }

        if(reservation.getStatus() != COMPLETED) {
            throw new GlobalException(RESERVATION_NOT_PROCESSED);
        }

        log.info("add review -> {}", request.getComments());
        reviewRepository.save(ReviewEntity.of(request, customer, reservation));
        return "해당 식당 " + "[" + reservation.getRestaurantEntity().getName() + "]" + "의 리뷰 남겼습니다";
    }

    /**
     * 리뷰 조회
     */
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
     * 리뷰 수정
     * 예약이 완료된 후에만 리뷰 수정 가능
     */
    @Transactional
    public String updateReview(ReviewDto request, Long reviewId, String header) {
        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new GlobalException(CUSTOMER_NOT_EXIST));

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GlobalException(REVIEW_NOT_EXIST));

        // 리뷰 수정은 예약이 완료된 후에만 가능
        if(!Objects.equals(review.getCustomerEntity().getId(), customer.getId())) {
            throw new GlobalException(REVIEW_NOT_YOURS);
        }

        review.setTitle(request.getTitle());
        review.setComment(request.getComments());
        review.setRating(request.getRating().getRateValue());

        reviewRepository.save(review);
        log.info("Review updated -> {}", request.getComments());
        return "리뷰가 성공적으로 업데이트되었습니다. [" + review.getRestaurantEntity().getName() + "]";
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public String deleteReviewManager(String header, Long reviewId) {

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
     * 리뷰 삭제
     */
    @Transactional
    public String deleteReviewCustomer(String header, Long reviewId) {

        String token = tokenProvider.getToken(header);
        Long customerId = tokenProvider.getId(token);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new GlobalException(CUSTOMER_NOT_EXIST));

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GlobalException(REVIEW_NOT_EXIST));

        if (!Objects.equals(review.getCustomerEntity().getId(), customer.getId())) {
            throw new GlobalException(REVIEW_NOT_YOURS);
        }

        reviewRepository.deleteById(reviewId);
        return "리뷰가 성공적으로 삭제되었습니다. [" + review.getRestaurantEntity().getName() + "]";
    }
}
