package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.RegisterRestaurantDto;
import project.restaurantmanagement.dto.ReservationDto;
import project.restaurantmanagement.dto.RestaurantDto;
import project.restaurantmanagement.service.ManagerService;

import java.util.List;

import static project.restaurantmanagement.model.Type.AcceptStatus.ACCEPT;
import static project.restaurantmanagement.model.Type.AcceptStatus.REFUSE;

/**
 * 매니저 관련 기능을 제공하는 컨트롤러
 */

@Slf4j
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private static final String AUTH_HEADER = "Authorization";

    /**
     * 식당 등록
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/add-restaurant")
    public ResponseEntity<?> addRestaurant(@RequestBody RegisterRestaurantDto registerDto,
                                           @RequestHeader(name = AUTH_HEADER) String header) {
        RestaurantDto restaurantInfo = managerService.createRestaurant(registerDto, header);
        log.info("restaurant added -> {} ", registerDto.getRestaurantName());
        return ResponseEntity.ok(restaurantInfo);
    }

    /**
     * 예약 목록 조회
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/reservations/{restaurantId}")
    public ResponseEntity<?> viewReservations(@RequestHeader(name = AUTH_HEADER) String header,
                                              @PathVariable Long restaurantId) {
        log.info("view reservations -> {} ", restaurantId);
        List<ReservationDto> reservationDtos = managerService.viewReservations(header, restaurantId);
        return ResponseEntity.ok(reservationDtos);
    }

    /**
     * 예약 승인
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/reservations/accept/{reservationId}")
    public ResponseEntity<?> acceptReservation(@RequestHeader(name = AUTH_HEADER) String header,
                                               @PathVariable Long reservationId) {

        log.info("accept reservation -> {} ", reservationId);
        return ResponseEntity.ok(managerService.acceptOrRefuseReservation(header, reservationId, ACCEPT));
    }

    /**
     * 예약 거절
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/reservations/refuse/{reservationId}")
    public ResponseEntity<?> declineReservation(@RequestHeader(name = AUTH_HEADER) String header,
                                               @PathVariable Long reservationId) {

        log.info("decline reservation -> {} ", reservationId);
        return ResponseEntity.ok(managerService.acceptOrRefuseReservation(header, reservationId, REFUSE));
    }

    /**
     * 리뷰 조회
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/review/{restaurantId}")
    public ResponseEntity<?> viewReviews(@RequestHeader(name = AUTH_HEADER) String header,
                                         @PathVariable Long restaurantId) {

        log.info("view reviews -> {} ", restaurantId);
        return ResponseEntity.ok(managerService.viewReviews(header, restaurantId));
    }

    /**
     * 리뷰 삭제
     */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<?> deleteReview(@RequestHeader(name = AUTH_HEADER) String header,
                                          @PathVariable Long reviewId) {

        log.info("delete review -> {} ", reviewId);
        return ResponseEntity.ok(managerService.deleteReview(header, reviewId));
    }
}
