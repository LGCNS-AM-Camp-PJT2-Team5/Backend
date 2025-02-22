package com.team9.jobbotdari.exception.userinterest;

import com.team9.jobbotdari.exception.base.BusinessException;
import com.team9.jobbotdari.exception.base.ExceptionCode;

public class DuplicateUserInterestException extends BusinessException {
    public DuplicateUserInterestException () {
        super(ExceptionCode.DUPLICATE_USER_INTEREST);
    }
}
