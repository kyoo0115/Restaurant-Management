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

/**
 * 식당 관련 요청을 처리하는 컨트롤러입니다.
 * 식당의 등록, 조회 및 방문 확인 기능을 제공합니다.
 */

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
     * 매니저가 식당을 등록하는 기능
     * 매니저 권한이 필요하며, 식당의 정보를 등록한 후 결과를 반환합니다.
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
     * 모든 식당의 정보를 조회하는 기능
     * 데이터베이스에서 모든 식당의 목록을 조회하여 반환합니다.
     */
    @GetMapping("/view")
    public ResponseEntity<?> viewRestaurants() {
        log.info("Viewing restaurants");
        var result = restaurantService.viewRestaurants();
        return ResponseEntity.ok(result);
    }

    /**
     * ID를 기반으로 특정 식당의 정보를 조회하는 기능
     * 주어진 ID에 해당하는 식당의 상세 정보를 조회하여 반환합니다.
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> viewRestaurant(@PathVariable Long restaurantId) {
        log.info("Viewing restaurant {}", restaurantId);
        var result = restaurantService.viewRestaurant(restaurantId);
        return ResponseEntity.ok(result);
    }

    /**
     * 고객이 식당을 방문한 것을 확인하는 기능
     * 고객 권한이 필요하며, 방문 정보를 기록하고 결과를 반환합니다.
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
