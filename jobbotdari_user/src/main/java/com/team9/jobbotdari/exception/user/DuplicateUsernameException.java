package com.team9.jobbotdari.exception.user;

import com.team9.jobbotdari.exception.base.BusinessException;
import com.team9.jobbotdari.exception.base.ExceptionCode;

public class DuplicateUsernameException extends BusinessException {
    public DuplicateUsernameException() {
        super(ExceptionCode.DUPLICATE_USERNAME);
    }
}