package com.team9.jobbotdari.exception.base;

import com.team9.jobbotdari.exception.signup.SignupException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final HttpStatus HTTP_STATUS_OK = HttpStatus.OK;

    // 회원가입 예외 처리 - 중복 아이디
    @ExceptionHandler(SignupException.DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(SignupException.DuplicateUsernameException e) {
        final ErrorResponse response = ErrorResponse.of(e.getCode());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 회원가입 예외 처리 - 비밀번호 불일치
    @ExceptionHandler(SignupException.PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(SignupException.PasswordMismatchException e) {
        final ErrorResponse response = ErrorResponse.of(e.getCode());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 비즈니스 로직 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleCustomException (BusinessException e) {
        final ErrorResponse response = ErrorResponse.of(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException (NullPointerException e) {
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.NULL_POINT_ERROR, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    // @Valid 유효성 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException (MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(":");
            sb.append(fieldError.getDefaultMessage());
            sb.append(", ");
        }
        final ErrorResponse response = ErrorResponse.of(ExceptionCode.NOT_VALID_ERROR, String.valueOf(sb));
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}
