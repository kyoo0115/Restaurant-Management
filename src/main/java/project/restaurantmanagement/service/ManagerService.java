package project.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.dto.*;
import project.restaurantmanagement.entity.ManagerEntity;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.exception.GlobalException;
import project.restaurantmanagement.repository.ManagerRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.security.TokenProvider;

import static project.restaurantmanagement.exception.ErrorCode.*;

/**
 * 매니저 관련 서비스를 제공하는 클래스입니다.
 * 매니저의 등록, 인증, 매장 및 예약 관리 등을 수행합니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService implements UserDetailsService {

    private final ManagerRepository managerRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * 사용자 이름(이메일)을 바탕으로 사용자 세부 정보를 로드합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return managerRepository.findByEmail(username).orElseThrow(
                () -> new GlobalException(USER_NOT_EXIST));
    }

    /**
     * 신규 매니저 등록
     * 비밀번호는 암호화하여 저장합니다.
     */
    @Transactional
    public SignUpDto.Response register(SignUpDto.Request signUpRequest) {
        if (managerRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new GlobalException(USER_NOT_EXIST);
        }

        log.info("register user -> {}", signUpRequest.getEmail());
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        log.info("password -> {}", signUpRequest.getPassword());

        ManagerEntity saved = managerRepository.save(ManagerEntity.from(signUpRequest));

        return SignUpDto.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }

    /**
     * 매니저 로그인 및 토큰 발행
     */
    @Transactional
    public SignInDto.Response authenticate(SignInDto.Request signInRequest) {
        ManagerEntity managerEntity = managerRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + signInRequest.getEmail()));

        if (!passwordEncoder.matches(signInRequest.getPassword(), managerEntity.getPassword())) {
            throw new GlobalException(WRONG_PASSWORD);
        }

        String token = tokenProvider.generateToken(managerEntity.getId(), managerEntity.getEmail(), managerEntity.getUserType());

        return SignInDto.Response.builder()
                .email(managerEntity.getEmail())
                .userType(managerEntity.getUserType())
                .token(token)
                .build();
    }

    /**
     * 매니저가 관리하는 매장 생성
     */
    @Transactional
    public RestaurantDto createRestaurant(RegisterRestaurantDto registerRestaurantDto, String header) {

        String token = tokenProvider.getToken(header);
        String email = tokenProvider.getUsername(token);

        ManagerEntity manager = managerRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        RestaurantEntity savedRestaurant = restaurantRepository.save(RestaurantEntity.of(registerRestaurantDto, manager));

        return RestaurantDto.from(savedRestaurant);
    }
}
