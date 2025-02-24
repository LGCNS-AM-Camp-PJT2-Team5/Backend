package com.team9.jobbotdari.dto.response;

import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentResponseDto {
    private Long id;
    private Long companyId;
    private String title;
    private String requirements;
    private String description;
    private LocalDateTime deadline;
}
