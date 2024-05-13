package project.restaurantmanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import project.restaurantmanagement.model.type.UserType;

/**
 * 로그인 요청 DTO
 */

public class SignInDto {

    @Getter
    @Setter
    public static class Request {

        private String email;
        private String password;
    }

    @Getter
    @Setter
    @Builder
    public static class Response {

        private String email;
        private UserType userType;
        private String token;
    }
}
