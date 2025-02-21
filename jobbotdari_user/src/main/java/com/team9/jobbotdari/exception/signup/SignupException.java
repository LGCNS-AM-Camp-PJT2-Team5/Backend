package com.team9.jobbotdari.exception.signup;

import com.team9.jobbotdari.exception.base.BusinessException;
import com.team9.jobbotdari.exception.base.ExceptionCode;

public class SignupException {

    public static class DuplicateUsernameException extends BusinessException {
        public DuplicateUsernameException() {
            super(ExceptionCode.DUPLICATE_USERNAME);
        }
    }

    public static class PasswordMismatchException extends BusinessException {
        public PasswordMismatchException() {
            super(ExceptionCode.PASSWORD_MISMATCH);
        }
    }
}