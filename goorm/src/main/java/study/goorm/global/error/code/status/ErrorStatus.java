package study.goorm.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import study.goorm.global.error.code.BaseErrorCode;
import study.goorm.global.error.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Cloth
    NO_SUCH_CLOTH(HttpStatus.BAD_REQUEST, "CLOTH_4001", "옷이 존재하지 않습니다"),
    NO_ClOTH_IMAGE(HttpStatus.BAD_REQUEST, "CLOTH_4002", "옷의 사진이 존재하지 않습니다."),
    NO_OWNED_CLOTH(HttpStatus.BAD_REQUEST, "CLOTH_4003", "본인의 옷이 아닌 옷이 등록되었습니다."),
    LOWER_TEMP_BIGGER_THAN_UPPER_TEMP(HttpStatus.BAD_REQUEST, "CLOTH_4004", "옷의 하한 온도가 상한 온도 보다 높습니다."),

    // Member
    NO_SUCH_MEMBER(HttpStatus.BAD_REQUEST, "MEMBER_4001", "멤버가 존재하지 않습니다."),

    // Page
    PAGE_UNDER_ONE(HttpStatus.BAD_REQUEST, "PAGE_4001", "페이지는 1이상으로 입력해야 합니다."),
    PAGE_SIZE_UNDER_ONE(HttpStatus.BAD_REQUEST, "PAGE_4002", "페이지 사이즈는 1이상으로 입력해야 합니다."),

    // Category
    NO_SUCH_CATEGORY(HttpStatus.BAD_REQUEST, "CLOTH_4003", "카테고리가 존재하지 않습니다."),

    // History
    NO_SUCH_HISTORY(HttpStatus.BAD_REQUEST, "HISTORY_4001", "존재하지 않는 기록 ID입니다."),
    BAD_DATE_TYPE(HttpStatus.BAD_REQUEST, "HISTORY_4001", "잘못된 날짜 형식입니다."),
    NO_HISTORY_IMAGE(HttpStatus.BAD_REQUEST, "HISTORY_4002", "기록의 사진이 존재하지 않습니다."),
    NO_IMAGE_SENT(HttpStatus.BAD_REQUEST, "HISTORY_4002", "기록의 사진은 한 장 이상이어야 합니다."),
    TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "HISTORY_4003", "기록의 사진은 10장 미만이어야 합니다."),
    CLOTHES_NOT_UNIQUE(HttpStatus.BAD_REQUEST, "HISTORY_4004", "중복되는 옷이 등록되었습니다."),
    HASHTAGS_NOT_UNIQUE(HttpStatus.BAD_REQUEST, "HISTORY_4005", "중복되는 해시태그가(hashtag List) 등록되었습니다."),
    HISTORY_UPDATE_DENIED(HttpStatus.BAD_REQUEST, "HISTORY_4006", "해당 사용자의 기록이 아닙니다."),
    NO_SUCH_COMMENT(HttpStatus.BAD_REQUEST, "HISTORY_4007", "존재하지 않는 댓글 ID입니다."),
    COMMENT_UPDATE_DENIED(HttpStatus.BAD_REQUEST, "HISTORY_4008", "해당 사용자의 댓글이 아닙니다.");


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
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
