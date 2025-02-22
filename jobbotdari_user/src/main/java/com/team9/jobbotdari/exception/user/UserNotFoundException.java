package com.team9.jobbotdari.exception.user;

import com.team9.jobbotdari.exception.base.BusinessException;
import com.team9.jobbotdari.exception.base.ExceptionCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ExceptionCode.USER_NOT_FOUND);
    }
}