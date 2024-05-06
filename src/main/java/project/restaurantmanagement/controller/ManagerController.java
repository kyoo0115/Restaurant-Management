package project.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.restaurantmanagement.dto.SignInDto;
import project.restaurantmanagement.dto.SignUpDto;
import project.restaurantmanagement.service.ManagerService;

/**
 * 매니저 관련 기능을 제공하는 컨트롤러입니다.
 * 매니저의 회원가입 및 로그인 기능을 관리합니다.
 */

@Slf4j
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    /**
     * 매니저 회원가입을 처리합니다.
     * 입력받은 매니저 정보를 시스템에 등록하고 결과를 반환합니다.
     *
     * @param request 매니저 등록 정보
     * @return 등록된 매니저 정보의 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto.Request request) {
        var result = this.managerService.register(request);
        log.info("manager signup -> {}", request.getEmail());
        return ResponseEntity.ok(result);
    }

    /**
     * 매니저 로그인을 처리합니다.
     * 이메일과 비밀번호를 통해 매니저를 인증하고 토큰을 발행합니다.
     *
     * @param request 로그인 요청 정보
     * @return 인증된 매니저 정보와 토큰
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto.Request request) {
        var user = this.managerService.authenticate(request);
        log.info("manager login -> {} ", user.getEmail());

        return ResponseEntity.ok(user);
    }
}
