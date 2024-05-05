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

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private static final String AUTH_HEADER = "Authorization";

    @GetMapping("/view-restaurants")
    public ResponseEntity<?> viewRestaurants() {
        log.info("view restaurants");
        var result = this.customerService.viewRestaurants();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-reservation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReservation(@RequestBody RegisterReservationDto registerDto,
                                               @RequestHeader(name = AUTH_HEADER) String token) {

        log.info("create reservation");
        var result = this.customerService.createReservation(registerDto, token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/visit-restaurant/{restaurantId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> visitRestaurant(@RequestBody VisitRestaurantDto request,
                                             @PathVariable Long restaurantId,
                                             @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("visit restaurant");
        String result = this.customerService.visitRestaurant(request, restaurantId, header);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/review/{reservationId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addReview(@RequestBody ReviewDto request,
                                       @PathVariable Long reservationId,
                                       @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("add review");
        String result = this.customerService.addReview(request, reservationId, header);
        return ResponseEntity.ok(result);
    }
}
