package project.restaurantmanagement.exception;

import lombok.Getter;

@Getter
public class ReservationServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ReservationServiceException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
