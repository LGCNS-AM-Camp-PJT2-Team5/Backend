package com.team9.jobbotdari.exception.user;

import com.team9.jobbotdari.exception.base.BusinessException;
import com.team9.jobbotdari.exception.base.ExceptionCode;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException() {
        super(ExceptionCode.PASSWORD_MISMATCH);
    }
}