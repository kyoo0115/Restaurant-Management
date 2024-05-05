package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.*;
import project.restaurantmanagement.security.AuthenticationService;
import project.restaurantmanagement.security.TokenProvider;
import project.restaurantmanagement.service.ManagerService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/add-restaurant")
    public ResponseEntity<?> addRestaurant(@RequestBody RegisterRestaurantDto registerDto,
                                           @RequestHeader("Authorization") String header) {
        RestaurantDto restaurantInfo = managerService.createRestaurant(registerDto, header);
        log.info("restaurant added -> {} ", registerDto.getRestaurantName());
        return ResponseEntity.ok(restaurantInfo);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/reservations/{restaurantId}")
    public ResponseEntity<?> viewReservations(@RequestHeader("Authorization") String header,
                                              @PathVariable Long restaurantId) {
        log.info("view reservations -> {} ", restaurantId);
        List<ReservationDto> reservationDtos = managerService.viewReservations(header, restaurantId);
        return ResponseEntity.ok(reservationDtos);
    }
}
