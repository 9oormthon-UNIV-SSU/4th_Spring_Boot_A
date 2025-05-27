package study.goorm.domain.history.exception;

import study.goorm.global.error.code.status.BaseErrorCode;
import study.goorm.global.exception.GeneralException;

public class HistoryException extends GeneralException {
    public HistoryException(BaseErrorCode code) {
        super(code);
    }
}
