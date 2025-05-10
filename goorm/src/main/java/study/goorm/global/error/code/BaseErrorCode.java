package study.goorm.global.error.code;

public interface BaseErrorCode {
    String getCode();

    String getMessage();

    ErrorReasonDTO getReasonHttpStatus();
}
