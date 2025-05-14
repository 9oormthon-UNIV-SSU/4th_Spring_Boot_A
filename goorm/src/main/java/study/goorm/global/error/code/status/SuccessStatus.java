package study.goorm.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    OK(HttpStatus.OK, "COMMON_200", "성공입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
