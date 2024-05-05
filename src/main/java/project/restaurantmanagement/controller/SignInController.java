package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.restaurantmanagement.dto.SignInDto;
import project.restaurantmanagement.service.CustomerService;
import project.restaurantmanagement.service.ManagerService;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
@Slf4j
public class SignInController {

    private final ManagerService managerService;
    private final CustomerService customerService;

    @PostMapping("/manager")
    public ResponseEntity<?> signInManager(@RequestBody SignInDto.Request request) {
        var user = this.managerService.authenticate(request);
        log.info("manager login -> {} ", user.getEmail());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/customer")
    public ResponseEntity<?> signInCustomer(@RequestBody SignInDto.Request request) {
        var user = this.customerService.authenticate(request);
        log.info("customer login -> {} ", user.getEmail());

        return ResponseEntity.ok(user);
    }
}
