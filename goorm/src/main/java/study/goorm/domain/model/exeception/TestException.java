package study.goorm.domain.model.exeception;

import study.goorm.global.error.code.BaseErrorCode;
import study.goorm.global.exception.GeneralException;

public class TestException extends GeneralException {

    public TestException(BaseErrorCode code) {
        super(code);
    }
}
