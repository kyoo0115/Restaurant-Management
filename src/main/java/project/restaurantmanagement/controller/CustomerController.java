package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.RegisterReservationDto;
import project.restaurantmanagement.dto.SignInDto;
import project.restaurantmanagement.dto.SignUpDto;
import project.restaurantmanagement.security.TokenProvider;
import project.restaurantmanagement.service.CustomerService;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/view-restaurants")
    public ResponseEntity<?> viewRestaurants() {
        var result = this.customerService.viewRestaurants();
        return ResponseEntity.ok(result);
    }

    @PostMapping("create-reservation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReservation(
            @RequestBody RegisterReservationDto registerDto,
            @RequestHeader("Authorization") String token) {

        var result = this.customerService.createReservation(registerDto, token);
        return ResponseEntity.ok(result);
    }

}
