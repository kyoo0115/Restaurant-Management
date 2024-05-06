package project.restaurantmanagement.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import project.restaurantmanagement.model.Type.UserType;
import project.restaurantmanagement.service.CustomerService;
import project.restaurantmanagement.service.ManagerService;

/**
 * JWT 토큰을 사용하여 인증 정보를 생성하고 관리합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationService {

    private final ManagerService managerService;
    private final CustomerService customerService;
    private final TokenProvider tokenProvider;

    /**
     * 주어진 JWT로부터 인증 객체를 생성합니다.
     */
    public Authentication getAuthentication(String jwt) {

        UserType userType = tokenProvider.getUserType(jwt);
        String username = tokenProvider.getUsername(jwt);

        UserDetails userDetails = getUserDetailsService(userType).loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 사용자 타입에 따라 적절한 UserDetailsService 구현을 반환합니다.
     */
    private UserDetailsService getUserDetailsService(UserType userType) {
        return userType == UserType.CUSTOMER ? customerService : managerService;
    }
}
