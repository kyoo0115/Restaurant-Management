package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.restaurantmanagement.dto.SignUpDto;
import project.restaurantmanagement.service.CustomerService;
import project.restaurantmanagement.service.ManagerService;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Slf4j
public class SignUpController {

    private final ManagerService managerService;
    private final CustomerService customerService;

    @PostMapping("/manager")
    public ResponseEntity<?> signUpManager(@RequestBody SignUpDto.Request request) {
        var result = this.managerService.register(request);
        log.info("manager signup -> {}", request.getEmail());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/customer")
    public ResponseEntity<?> signUpCustomer(@RequestBody SignUpDto.Request request) {
        var result = this.customerService.register(request);
        log.info("customer signup -> {}", request.getEmail());
        return ResponseEntity.ok(result);
    }
}
