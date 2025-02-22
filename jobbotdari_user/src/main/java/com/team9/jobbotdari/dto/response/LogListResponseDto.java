package com.team9.jobbotdari.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LogListResponseDto {
    private Long userId;
    private String name;
    private String description;
    private String action;
    private LocalDateTime createdAt;
}
