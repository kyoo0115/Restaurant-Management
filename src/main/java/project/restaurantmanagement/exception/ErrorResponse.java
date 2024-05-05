package project.restaurantmanagement.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private String message;
}
