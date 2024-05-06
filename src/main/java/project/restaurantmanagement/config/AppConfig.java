package project.restaurantmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 애플리케이션 전반에 걸쳐 사용될 공통 구성 요소를 정의하는 설정 클래스입니다.
 */
@Configuration
public class AppConfig {

    /**
     * 비밀번호 암호화를 위한 Encoder를 Bean으로 등록합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
