package study.goorm.domain.history.exception;

import study.goorm.global.error.code.BaseErrorCode;
import study.goorm.global.exception.GeneralException;

public class HistoryExeption extends GeneralException {
    public HistoryExeption(BaseErrorCode code) {
        super(code);
    }
}
