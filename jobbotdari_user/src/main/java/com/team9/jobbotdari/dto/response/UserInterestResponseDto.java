package com.team9.jobbotdari.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInterestResponseDto {
   private Long userId;
   private List<Long> companyIds;
}
