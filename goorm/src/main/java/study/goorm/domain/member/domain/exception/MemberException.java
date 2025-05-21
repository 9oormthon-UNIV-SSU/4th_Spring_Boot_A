package study.goorm.domain.member.domain.exception;

import study.goorm.global.error.code.status.BaseErrorCode;
import study.goorm.global.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(BaseErrorCode code) {
        super(code);
    }
}