package study.goorm.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.global.error.code.BaseCode;
import study.goorm.global.error.code.ReasonDTO;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    //Common
    OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    //Cloth
    CLOTH_VIEW_SUCCESS(HttpStatus.OK,"CLOTH_200","옷이 성공적으로 조회되었습니다."),
    CLOTH_CREATED(HttpStatus.CREATED, "CLOTH_201"," 옷이 성공적으로 생성되었습니다."),
    CLOTH_DELETED(HttpStatus.NO_CONTENT,"CLOTH_202","옷이 성공적으로 삭제되었습니다"),

    //History
    HISTORY_GET_MONTH(HttpStatus.OK, "HISTORY_200","월별 기록이 성공적으로 조회되었습니다."),
    HISTORY_CREATED(HttpStatus.CREATED,"HISTORY_201","옷 기록이 성공적으로 생성되었습니다."),
    HISTORY_DELETED(HttpStatus.NO_CONTENT, "HISTORY_202","기록이 성공적으로 삭제되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
