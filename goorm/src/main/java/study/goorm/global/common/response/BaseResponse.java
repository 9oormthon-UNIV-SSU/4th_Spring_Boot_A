package study.goorm.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import study.goorm.global.error.code.status.ErrorStatus;
import study.goorm.global.error.code.status.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> { // 어떤 타입이든 result로 받도록 설계
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL) // null이면 JSON 응답에 포함되지 않도록 설정됨
    private final T result; // 실제 반환하고 싶은 데이터

    public static <T> BaseResponse<T> onSuccess(SuccessStatus status, T result){
        return new BaseResponse<>(
                true,
                status.getCode(),
                status.getMessage(),
                result);
    }

    public static <T> BaseResponse<T> onFailure(ErrorStatus status, T result){
        return new BaseResponse<>(
                false,
                status.getCode(),
                status.getMessage(),
                result);
    }
}
