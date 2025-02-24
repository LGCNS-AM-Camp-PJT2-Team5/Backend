package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.RecruitmentRequestDto;
import com.team9.jobbotdari.dto.response.RecruitmentResponseDto;
import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.entity.Recruitment;
import com.team9.jobbotdari.repository.RecruitmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final CompanyService companyService;
    private final ApiRequestService apiRequestService;

    // 컨트롤러용 메소드
    public List<RecruitmentResponseDto> getAllRecruitments() {
        List<Recruitment> recruitments = recruitmentRepository.findAll();
        return recruitments.stream().map(this::mapToRecruitmentDto).collect(Collectors.toList());
    }

    // 컨트롤러용 메소드
    public RecruitmentResponseDto getRecruitmentById(Long id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruitment not found with id: " + id));
        return mapToRecruitmentDto(recruitment);
    }

    // 스케쥴러용 메소드
    @Transactional
    public void addRecruitment() {
        recruitmentRepository.deleteAll();
        List<RecruitmentRequestDto> recruitmentDtos = apiRequestService.getSaraminRecruitmentsInfo();
        for (RecruitmentRequestDto recruitmentDto : recruitmentDtos) {
            Company company = companyService.getCompanyByName(recruitmentDto.getCompanyName());
            if (company == null) {
                company = companyService.addCompany(recruitmentDto.getCompanyName());
            }

            Recruitment recruitment = Recruitment.builder()
                    .id(recruitmentDto.getId())
                    .company(company)
                    .title(recruitmentDto.getTitle())
                    .requirements(recruitmentDto.getRequirements())
                    .description(recruitmentDto.getDescription())
                    .deadline(recruitmentDto.getDeadline())
                    .build();
            recruitmentRepository.save(recruitment);
        }
    }

    private RecruitmentResponseDto mapToRecruitmentDto(Recruitment recruitment) {
        return new RecruitmentResponseDto(
                recruitment.getId(),
                recruitment.getCompany().getId(),
                recruitment.getTitle(),
                recruitment.getRequirements(),
                recruitment.getDescription(),
                recruitment.getDeadline()
        );
    }
}
