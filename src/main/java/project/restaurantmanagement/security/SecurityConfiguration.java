package project.restaurantmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 스프링 시큐리티 설정을 담당하는 클래스입니다.
 * HTTP 보안, 세션 관리, 인증 요청 경로 설정 등을 정의합니다.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * HTTP 보안 설정을 구성합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 로그인 방식 비활성화
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .sessionManagement(e -> e.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)) // 세션을 상태 없이 관리
                .authorizeHttpRequests(e -> e
                        .requestMatchers(
                                "/customer/signin", "/customer/signup",
                                "/manager/signin", "/manager/signup",
                                "/restaurants/view")
                        .permitAll() // 지정된 경로는 인증 없이 접근 허용
                        .anyRequest()
                        .authenticated()) // 그 외 모든 요청은 인증 필요
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    /**
     * AuthenticationManager를 Bean으로 등록하여 인증 관리자를 설정합니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
