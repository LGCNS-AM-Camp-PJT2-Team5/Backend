package com.team9.jobbotdari.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class SigninResponseDto {
    private String userRole;
    private String accessToken;
}
