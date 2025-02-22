package com.team9.jobbotdari.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public class BaseResponseDto {
    public Object data;
    public int code;
}
