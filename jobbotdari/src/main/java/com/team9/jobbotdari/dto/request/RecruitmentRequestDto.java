package com.team9.jobbotdari.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RecruitmentRequestDto {
    private Long id;
    private String companyName;
    private String title;
    private String requirements;
    private String description;
    private LocalDateTime deadline;
}
