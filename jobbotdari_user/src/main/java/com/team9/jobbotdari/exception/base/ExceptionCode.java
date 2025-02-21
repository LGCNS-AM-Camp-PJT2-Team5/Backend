package com.team9.jobbotdari.exception.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    USER_NOT_FOUND(404, "USER_004", "해당 유저를 찾을 수 없습니다."),

    NULL_POINT_ERROR(404, "G010", "NullPointerException 발생"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음.
    NOT_VALID_ERROR(404, "G011", "Validation Exception 발생");

    /**
     *
     * status - 상태 코드
     * code - 오류 카테고리화. (예외 원인 식별을 위함)
     * message - 발생한 예외 설명
     *
     */

    private final int status;
    private final String code;
    private final String message;
}
