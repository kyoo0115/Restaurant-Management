package project.restaurantmanagement.exception;

import lombok.Getter;

/**
 * 애플리케이션 전역에서 사용할 사용자 정의 예외 클래스입니다.
 * 에러 코드와 함께 예외를 관리합니다.
 */
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 에러 코드를 받아 예외 메시지를 설정합니다.
     */
    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
