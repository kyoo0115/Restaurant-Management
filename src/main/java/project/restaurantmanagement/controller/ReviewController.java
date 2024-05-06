package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.ReviewDto;
import project.restaurantmanagement.service.ReviewService;

/**
 * 리뷰 관련 요청을 처리하는 컨트롤러입니다.
 * 리뷰의 추가, 수정, 삭제 및 조회 기능을 제공합니다.
 */

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private static final String AUTH_HEADER = "Authorization";

    /**
     * 고객의 예약에 대한 리뷰를 추가합니다.
     * 고객 권한이 필요하며, 리뷰 데이터를 받아 처리합니다.
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
     * 고객의 리뷰를 삭제합니다.
     * 고객 권한이 필요하며, 삭제할 리뷰의 ID를 받아 처리합니다.
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
     * 고객의 리뷰를 삭제합니다.
     * 고객 권한이 필요하며, 삭제할 리뷰의 ID를 받아 처리합니다.
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
     * 점장이 자신의 매장에 대한 리뷰를 조회합니다.
     * 점장 권한이 필요하며, 조회할 매장의 ID를 받아 처리합니다.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> viewReviews(@RequestHeader(name = AUTH_HEADER) String header,
                                         @PathVariable Long restaurantId) {

        log.info("view reviews -> {} ", restaurantId);
        return ResponseEntity.ok(reviewService.viewReviews(header, restaurantId));
    }

    /**
     * 점장이 리뷰를 삭제합니다.
     * 점장 권한이 필요하며, 삭제할 리뷰의 ID를 받아 처리합니다.
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
