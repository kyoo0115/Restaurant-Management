package project.restaurantmanagement.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import project.restaurantmanagement.model.Constants.UserType;

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
