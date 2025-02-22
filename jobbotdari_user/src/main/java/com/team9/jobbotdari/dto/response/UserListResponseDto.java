package com.team9.jobbotdari.dto.response;

import com.team9.jobbotdari.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserListResponseDto {
    private Long id;
    private String username;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
