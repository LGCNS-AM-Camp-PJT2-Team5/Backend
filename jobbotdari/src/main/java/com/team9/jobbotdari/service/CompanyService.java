package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.CompanyRequestDto;
import com.team9.jobbotdari.dto.response.CompanyResponseDto;
import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final NewsService newsService;
    private final NewsSummaryService newsSummaryService;

    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream().map(this::mapToCompanyDto).collect(Collectors.toList());
    }

    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        return mapToCompanyDto(company);
    }

    public void addCompany(CompanyRequestDto companyRequestDto) {
        Company company = Company.builder()
                .name(companyRequestDto.getName())
                .websiteUrl(companyRequestDto.getWebsiteUrl())
                .build();

        companyRepository.save(company);
    }

    private CompanyResponseDto mapToCompanyDto(Company company) {
        CompanyResponseDto companyResponseDto = new CompanyResponseDto();
        companyResponseDto.setId(company.getId());
        companyResponseDto.setName(company.getName());
        companyResponseDto.setDescription(company.getDescription());

        return companyResponseDto;
    }

}
