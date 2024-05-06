package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.RegisterRestaurantDto;
import project.restaurantmanagement.dto.RestaurantDto;
import project.restaurantmanagement.dto.VisitRestaurantDto;
import project.restaurantmanagement.service.CustomerService;
import project.restaurantmanagement.service.ManagerService;
import project.restaurantmanagement.service.RestaurantService;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final CustomerService customerService;
    private final ManagerService managerService;
    private static final String AUTH_HEADER = "Authorization";

    /**
     * 식당 등록
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/add")
    public ResponseEntity<?> addRestaurant(@RequestBody RegisterRestaurantDto registerDto,
                                           @RequestHeader(name = AUTH_HEADER) String header) {
        RestaurantDto restaurantInfo = managerService.createRestaurant(registerDto, header);
        log.info("restaurant added -> {} ", registerDto.getRestaurantName());
        return ResponseEntity.ok(restaurantInfo);
    }

    /**
     * 모든 식당 조회
     */
    @GetMapping("/view")
    public ResponseEntity<?> viewRestaurants() {
        log.info("Viewing restaurants");
        var result = restaurantService.viewRestaurants();
        return ResponseEntity.ok(result);
    }

    /**
     * id 값으로 식당 조회
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> viewRestaurant(@PathVariable Long restaurantId) {
        log.info("Viewing restaurant {}", restaurantId);
        var result = restaurantService.viewRestaurant(restaurantId);
        return ResponseEntity.ok(result);
    }

    /**
     * 식당 방문 확인
     */
    @PostMapping("/visit/{restaurantId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> visitRestaurant(@RequestBody VisitRestaurantDto request,
                                             @PathVariable Long restaurantId,
                                             @RequestHeader(name = AUTH_HEADER) String header) {

        log.info("visit restaurant");
        String result = this.customerService.visitRestaurant(request, restaurantId, header);
        return ResponseEntity.ok(result);
    }
}
