package study.goorm.domain.cloth.exception;

import study.goorm.global.error.code.BaseErrorCode;
import study.goorm.global.exception.GeneralException;

public class ClothException extends GeneralException {
    public ClothException(BaseErrorCode code) {
        super(code);
    }
}
