package study.goorm.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    // Common
    OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    // Cloth
    CLOTH_VIEW_SUCCESS(HttpStatus.OK,"CLOTH_200","옷이 성공적으로 조회되었습니다."),
    CLOTH_CREATED(HttpStatus.CREATED, "CLOTH_201"," 옷이 성공적으로 생성되었습니다."),
    CLOTH_DELETED(HttpStatus.NO_CONTENT,"CLOTH_202","옷이 성공적으로 삭제되었습니다"),

    // History
    HISTORY_VIEW_SUCCESS(HttpStatus.OK, "HISTORY_200", "월별 기록이 성공적으로 조회되었습니다."),
    HISTORY_CREATED(HttpStatus.OK, "HISTORY_201", "기록이 성공적으로 추가되었습니다."),
    HISTORY_PATCHED(HttpStatus.OK, "HISTORY_202", "기록이 성공적으로 수정되었습니다."),
    HISTORY_DELETED(HttpStatus.OK, "HISTORY_204", "기록이 성공적으로 삭제되었습니다."),

    // Comment
    COMMENT_VIEW_SUCCESS(HttpStatus.OK, "COMMENT_200", "댓글이 성공적으로 조회되었습니다."),
    COMMENT_CREATED(HttpStatus.OK, "COMMENT_201", "댓글이 성공적으로 생성되었습니다."),
    COMMENT_PATCHED(HttpStatus.OK, "COMMENT_202", "댓글이 성공적으로 수정되었습니다."),
    COMMENT_DELETED(HttpStatus.OK, "COMMENT_204", "댓글이 성공적으로 삭제되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private fi