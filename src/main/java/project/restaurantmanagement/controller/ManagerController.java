package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.SignInDto;
import project.restaurantmanagement.dto.SignUpDto;
import project.restaurantmanagement.service.ManagerService;

/**
 * 매니저 관련 기능을 제공하는 컨트롤러
 */

@Slf4j
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto.Request request) {
        var result = this.managerService.register(request);
        log.info("manager signup -> {}", request.getEmail());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto.Request request) {
        var user = this.managerService.authenticate(request);
        log.info("manager login -> {} ", user.getEmail());

        return ResponseEntity.ok(user);
    }
}
