package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.RegisterReservationDto;
import project.restaurantmanagement.dto.ReservationDto;
import project.restaurantmanagement.service.CustomerService;
import project.restaurantmanagement.service.ReservationService;

import java.util.List;

import static project.restaurantmanagement.model.Type.AcceptStatus.ACCEPT;
import static project.restaurantmanagement.model.Type.AcceptStatus.REFUSE;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final CustomerService customerService;
    private final ReservationService reservationService;
    private static final String AUTH_HEADER = "Authorization";

    /**
     * 신규 예약 생성
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReservation(@RequestBody RegisterReservationDto registerDto,
                                               @RequestHeader(AUTH_HEADER) String header) {
        log.info("Creating reservation");
        var result = customerService.createReservation(registerDto, header);
        return ResponseEntity.ok(result);
    }

    /**
     * 예약 목록 조회
     */
    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> viewReservations(@PathVariable Long restaurantId,
                                              @RequestHeader(AUTH_HEADER) String header) {
        log.info("Viewing reservations for restaurant {}", restaurantId);
        List<ReservationDto> reservationDtos = reservationService.viewReservations(header, restaurantId);
        return ResponseEntity.ok(reservationDtos);
    }

    /**
     * 예약 승인
     */
    @PatchMapping("/accept/{reservationId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> acceptReservation(@PathVariable Long reservationId,
                                               @RequestHeader(AUTH_HEADER) String header) {
        log.info("Accepting reservation {}", reservationId);
        return ResponseEntity.ok(reservationService.acceptOrRefuseReservation(header, reservationId, ACCEPT));
    }

    /**
     * 예약 거절
     */
    @PatchMapping("/refuse/{reservationId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> declineReservation(@PathVariable Long reservationId,
                                                @RequestHeader(AUTH_HEADER) String header) {
        log.info("Declining reservation {}", reservationId);
        return ResponseEntity.ok(reservationService.acceptOrRefuseReservation(header, reservationId, REFUSE));
    }
}
