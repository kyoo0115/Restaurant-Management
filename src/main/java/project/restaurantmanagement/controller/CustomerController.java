package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.*;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto.Request request) {
        var result = this.customerService.register(request);
        log.info("customer signup -> {}", request.getEmail());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto.Request request) {
        var user = this.customerService.authenticate(request);
        log.info("customer login -> {} ", user.getEmail());

        return ResponseEntity.ok(user);
    }
}
