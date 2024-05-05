package project.restaurantmanagement.security;

import io.jsonwebtoken.Claims;
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

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationService {

    private final ManagerService managerService;
    private final CustomerService customerService;
    private final TokenProvider tokenProvider;

    public Authentication getAuthentication(String jwt) {

        UserType userType = getUserType(jwt);
        String username = tokenProvider.getUsername(jwt);

        UserDetails userDetails = getUserDetailsService(userType).loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private UserDetailsService getUserDetailsService(UserType userType) {
        return userType == UserType.CUSTOMER ? customerService : managerService;
    }

    private UserType getUserType(String token) {
        Claims claims = tokenProvider.parseClaims(token);

        return UserType.valueOf(claims.get("roles", String.class));
    }
}
