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
    NO_SUCH_CLOTH(HttpStatus.BAD_REQUEST, "CLOTH_4001","옷이 존재하지 않습니다"),
    NO_ClOTH_IMAGE(HttpStatus.BAD_REQUEST,"CLOTH_4002","옷의 사진이 존재하지 않습니다."),
    // Member
    NO_SUCH_MEMBER(HttpStatus.BAD_REQUEST,"MEMBER_4003","멤버가 존재하지 않습니다."),

    // Page
    PAGE_UNDER_ONE(HttpStatus.BAD_REQUEST,"PAGE_4001","페이지는 1이상으로 입력해야 합니다."),
    PAGE_SIZE_UNDER_ONE(HttpStatus.BAD_REQUEST,"PAGE_4002","페이지 사이즈는 1이상으로 입력해야 합니다."),

    // ErrorStatus에 에러 추가 !
    NO_SUCH_CATEGORY(HttpStatus.BAD_REQUEST, "CLOTH_4003", "카테고리가 존재하지 않습니다."),
    LOWER_TEMP_BIGGER_THAN_UPPER_TEMP(HttpStatus.BAD_REQUEST,"CLOTH_4004","옷의 하한 온도가 상한 온도 보다 높습니다."),

    // History
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "HISTORY_4001", "잘못된 날짜 형식입니다."),
    NO_SUCH_HISTORY(HttpStatus.BAD_REQUEST, "HISTORY_4002","존재하지 않는 기록 ID 입니다."),
    TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "HISTORY_4002","이미지 업로드 개수를 초과했습니다"),
    NO_PERMISSION_FOR_RECORD(HttpStatus.BAD_REQUEST, "HISTORY_4006","기록에 접근 권한이 없습니다."),
    INVALID_LIKED(HttpStatus.BAD_REQUEST,"HISTORY_4004","잘못된 isLiked 값을 입력했습니다."),
    ;
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
