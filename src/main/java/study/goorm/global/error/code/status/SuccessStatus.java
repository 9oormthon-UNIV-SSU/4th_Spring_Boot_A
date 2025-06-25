package study.goorm.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode{

    // Common
    OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    // Cloth
    CLOTH_VIEW_SUCCESS(HttpStatus.OK, "CLOTH_200", "옷이 성공적으로 조회되었습니다."),
    CLOTH_CREATED(HttpStatus.CREATED, "CLOTH_201", "옷이 성공적으로 생성되었습니다."),
    CLOTH_DELETED(HttpStatus.NO_CONTENT, "CLOTH_202", "옷이 성공적으로 삭제되었습니다."),

    // History
    HISTORY_VIEW_SUCCESS(HttpStatus.OK, "HISTORY_200", "기록이 성공적으로 조회되었습니다."),
    HISTORY_CREATED(HttpStatus.CREATED, "HISTORY_201", "기록이 성공적으로 추가되었습니다."),
    HISTORY_UPDATED(HttpStatus.OK, "HISTORY_200", "기록이 성공적으로 수정되었습니다."),
    HISTORY_DELETED(HttpStatus.NO_CONTENT, "HISTORY_202", "기록이 성공적으로 삭제되었습니다."),
    HISTORY_LIKE_CREATED(HttpStatus.CREATED, "HISTORY_LIKE_201", "좋아요 상태가 성공적으로 변경되었습니다."),
    HISTORY_COMMENT_CREATED(HttpStatus.CREATED, "HISTORY_COMMENT_201", "댓글이 성공적으로 생성되었습니다.");

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
