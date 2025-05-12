package study.goorm.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import study.goorm.global.error.code.status.BaseCode;
import study.goorm.global.error.code.status.BaseErrorCode;
import study.goorm.global.error.code.status.ErrorStatus;
import study.goorm.global.error.code.status.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> BaseResponse<T> onSuccess(BaseCode status, T result) {
        return new BaseResponse<>(
                true,
                status.getCode(),
                status.getMessage(),
                result);
    }

    public static <T> BaseResponse<T> onFailure(BaseErrorCode status, T result) {
        return new BaseResponse<>(
                false,
                status.getCode(),
                status.getMessage(),
                result);
    }
}