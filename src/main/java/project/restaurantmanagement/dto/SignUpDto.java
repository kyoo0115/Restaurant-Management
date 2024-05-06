package project.restaurantmanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 DTO
 */

public class SignUpDto {

    @Getter
    @Setter
    public static class Request {

        private String email;
        private String name;
        private String password;
        private String phoneNumber;
    }

    @Getter
    @Setter
    @Builder
    public static class Response {

        private Long id;
        private String email;
        private String name;
    }
}
