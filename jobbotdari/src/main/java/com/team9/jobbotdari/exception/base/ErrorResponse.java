package com.team9.jobbotdari.exception.base;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int status;
    private String code;
    private String message;
    private List<FieldErrorResponse> errors;
    private String reason;

    @Builder
    public ErrorResponse(final ExceptionCode code) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = new ArrayList<>();
    }

    @Builder
    protected ErrorResponse(final ExceptionCode code, final String reason) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.reason = reason;
        this.errors = new ArrayList<>();
    }

    @Builder
    protected ErrorResponse(final ExceptionCode code, final List<FieldErrorResponse> errors) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();  // 수정: 코드에서 제공하는 메시지를 사용
        this.errors = errors;
    }

    public static ErrorResponse of(final ExceptionCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldErrorResponse.of(bindingResult));
    }

    @Builder
    public static ErrorResponse of(final ExceptionCode code) {
        return new ErrorResponse(code);
    }

    @Builder
    public static ErrorResponse of(final ExceptionCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }

    // 필드 오류 처리
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldErrorResponse {
        private String field;
        private String value;
        private String reason;

        @Builder
        public FieldErrorResponse(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldErrorResponse> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldErrorResponse(
                            error.getField(),
                            error.getRejectedValue() != null ? error.getRejectedValue().toString() : "",
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}