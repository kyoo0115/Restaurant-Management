package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.*;
import project.restaurantmanagement.service.CustomerService;

/**
 * 고객 관련 요청을 처리하는 컨트롤러입니다.
 * 고객의 회원가입 및 로그인을 관리합니다.
 */

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 회원가입을 처리합니다.
     * 입력받은 사용자 정보를 등록하고 결과를 반환합니다.
     *
     * @param request 사용자 등록 정보
     * @return 등록된 사용자 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto.Request request) {
        var result = this.customerService.register(request);
        log.info("customer signup -> {}", request.getEmail());
        return ResponseEntity.ok(result);
    }

    /**
     * 고객 로그인을 처리합니다.
     * 이메일과 비밀번호를 통해 사용자를 인증하고 토큰을 발행합니다.
     *
     * @param request 로그인 요청 정보
     * @return 인증된 사용자 정보와 토큰
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto.Request request) {
        var user = this.customerService.authenticate(request);
        log.info("customer login -> {} ", user.getEmail());

        return ResponseEntity.ok(user);
    }
}
