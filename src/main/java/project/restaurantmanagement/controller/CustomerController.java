package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.RegisterReservationDto;
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
        var result = this.customerService.viewRestaurants();
        return ResponseEntity.ok(result);
    }

    @PostMapping("create-reservation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReservation(
            @RequestBody RegisterReservationDto registerDto,
            @RequestHeader(name = AUTH_HEADER) String token) {

        var result = this.customerService.createReservation(registerDto, token);
        return ResponseEntity.ok(result);
    }
}
