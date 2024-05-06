package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.ReviewDto;
import project.restaurantmanagement.service.CustomerService;
import project.restaurantmanagement.service.ManagerService;
import project.restaurantmanagement.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private static final String AUTH_HEADER = "Authorization";

    /**
     * 회원 예약에 대한 리뷰 추가
     */
    @PostMapping("/{reservationId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto request,
                                       @PathVariable Long reservationId,
                                       @RequestHeader(AUTH_HEADER) String header) {
        log.info("Adding review for reservation {}", reservationId);
        String result = reviewService.addReview(request, reservationId, header);
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 리뷰 업데이트
     */
    @PatchMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDto request,
                                          @PathVariable Long reviewId,
                                          @RequestHeader(AUTH_HEADER) String header) {
        log.info("Updating review {}", reviewId);
        String result = reviewService.updateReview(request, reviewId, header);
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteReviewCustomer(@PathVariable Long reviewId,
                                          @RequestHeader(AUTH_HEADER) String header) {
        log.info("Deleting review {}", reviewId);
        String result = reviewService.deleteReviewCustomer(header, reviewId);
        return ResponseEntity.ok(result);
    }

    /**
     * 점장 리뷰 조회
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> viewReviews(@RequestHeader(name = AUTH_HEADER) String header,
                                         @PathVariable Long restaurantId) {

        log.info("view reviews -> {} ", restaurantId);
        return ResponseEntity.ok(reviewService.viewReviews(header, restaurantId));
    }

    /**
     * 점장 리뷰 삭제
     */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/manager/{reviewId}")
    public ResponseEntity<?> deleteReviewManager(@RequestHeader(name = AUTH_HEADER) String header,
                                                 @PathVariable Long reviewId) {

        log.info("delete review -> {} ", reviewId);
        String result = reviewService.deleteReviewManager(header, reviewId);
        return ResponseEntity.ok(result);
    }
}
