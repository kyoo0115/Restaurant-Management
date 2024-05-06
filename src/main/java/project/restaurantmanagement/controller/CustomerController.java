package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.RegisterReservationDto;
import project.restaurantmanagement.dto.ReviewDto;
import project.restaurantmanagement.dto.VisitRestaurantDto;
import project.restaurantmanagement.service.CustomerService;

/**
 * 고객 관련 요청을 처리하는 컨트롤러입니다.
 */

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private static final String AUTH_HEADER = "Authorization";

    /**
     * 모든 식당 조회
     */
    @GetMapping("/view-restaurants")
    public ResponseEntity<?> viewRestaurants() {
        log.info("view restaurants");
        var result = this.customerService.viewRestaurants();
        return ResponseEntity.ok(result);
    }

    /**
     * 신규 예약 생성
     */
    @PostMapping("/create-reservation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReservation(@RequestBody RegisterReservationDto registerDto,
                                               @RequestHeader(name = AUTH_HEADER) String token) {

        log.info("create reservation");
        var result = this.customerService.createReservation(registerDto, token);
        return ResponseEntity.ok(result);
    }

    /**
     * 식당 방문 확인
     */
    @PostMapping("/visit-restaurant/{restaurantId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> visitRestaurant(@RequestBody VisitRestaurantDto request,
                                             @PathVariable Long restaurantId,
                                             @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("visit restaurant");
        String result = this.customerService.visitRestaurant(request, restaurantId, header);
        return ResponseEntity.ok(result);
    }

    /**
     * 예약에 대한 리뷰 추가
     */
    @PostMapping("/review/{reservationId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto request,
                                       @PathVariable Long reservationId,
                                       @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("add review");
        String result = this.customerService.addReview(request, reservationId, header);
        return ResponseEntity.ok(result);
    }

    /**
     * 리뷰 업데이트
     */
    @PatchMapping("/review/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateReview(@RequestBody ReviewDto request,
                                          @PathVariable Long reviewId,
                                          @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("update review");
        String result = this.customerService.updateReview(request, reviewId, header);
        return ResponseEntity.ok(result);
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/review/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                          @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("delete review");
        String result = this.customerService.deleteReview(reviewId, header);
        return ResponseEntity.ok(result);
    }
}
