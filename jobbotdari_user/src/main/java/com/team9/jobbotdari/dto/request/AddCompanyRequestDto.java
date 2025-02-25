package com.team9.jobbotdari.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyRequestDto {
    private String name;
    private String websiteUrl;
}
