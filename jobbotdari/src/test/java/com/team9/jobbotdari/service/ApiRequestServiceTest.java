package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.RecruitmentRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ApiRequestServiceTest {
    @Autowired
    private ApiRequestService apiRequestService;

    @Test
    void testGetSaraminRecruitmentsInfo() {
        List<RecruitmentRequestDto> response = apiRequestService.getSaraminRecruitmentsInfo();

        if (response.isEmpty()) {
            System.out.println("채용 정보가 없습니다.");
        } else {
            System.out.println("채용 정보 리스트:");
            response.forEach(dto -> System.out.println(dtoToString(dto)));
        }
    }

    private String dtoToString(RecruitmentRequestDto dto) {
        return String.format(
                "ID: %d\n" +
                        "회사명: %s\n" +
                        "공고 제목: %s\n" +
                        "채용 형태: %s\n" +
                        "직무 설명: %s\n" +
                        "마감일: %s\n",
                dto.getId(),
                dto.getCompanyName(),
                dto.getTitle(),
                dto.getRequirements(),
                dto.getDescription(),
                dto.getDeadline() != null ? dto.getDeadline().toString() : "마감일 없음"
        );
    }
}

