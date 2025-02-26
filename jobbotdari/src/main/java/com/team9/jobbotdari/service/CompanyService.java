package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.NewsDto;
import com.team9.jobbotdari.dto.request.CompanyRequestDto;
import com.team9.jobbotdari.dto.response.CompanyResponseDto;
import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final NewsService newsService;
    private final ApiRequestService apiRequestService;

    // 컨트롤러용 메소드
    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream().map(this::mapToCompanyDto).collect(Collectors.toList());
    }

    // 컨트롤러용 메소드
    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        return mapToCompanyDto(company);
    }

    // 컨트롤러용 메소드
    public Company addCompany(CompanyRequestDto companyRequestDto) {
        Company company = Company.builder()
                .name(companyRequestDto.getName())
                .websiteUrl(companyRequestDto.getWebsiteUrl())
                .build();
        return companyRepository.save(company);
    }

    // 컨트롤러용 메소드 (오버로딩)
    @Transactional
    public void updateCompanyDescription(Company company) {
        // 회사 이름을 검색어로 사용하여 뉴스 기사 리스트를 조회합니다.
        // 뉴스 검색어 변경 필요 시, 해당 부분 수정
        List<NewsDto> newsList = newsService.searchNews(company.getName());     // ex) company.getName() + "채용"
        // 뉴스 기사들의 타이틀에 번호를 붙여 하나의 문자열로 결합합니다.
        String titlesSummaryInput = newsService.generateTitlesSummaryInput(newsList);
        // 결합된 타이틀 문자열을 기반으로 뉴스 요약을 생성합니다.
        String summary = (String) apiRequestService.getNewsSummary(titlesSummaryInput).get("description");
        // 생성된 요약을 회사의 description 필드에 업데이트합니다.
        company.setDescription(summary);
        // 업데이트된 회사를 저장합니다.
        companyRepository.save(company);
    }

    // 스케쥴러용 메소드 (오버로딩)
    @Transactional
    public void updateCompanyDescription() throws InterruptedException {
        // 1. 모든 회사 엔티티를 조회합니다.
        List<Company> companies = companyRepository.findAll();

        // 2. 각 회사를 순회하면서 뉴스 검색 및 요약 업데이트를 수행합니다.
        for (Company company : companies) {
            // 회사 이름을 검색어로 사용하여 뉴스 기사 리스트를 조회합니다.
            // 뉴스 검색어 변경 필요 시, 해당 부분 수정
            List<NewsDto> newsList = newsService.searchNews(company.getName());     // ex) company.getName() + "채용"
            // 뉴스 기사들의 타이틀에 번호를 붙여 하나의 문자열로 결합합니다.
            String titlesSummaryInput = newsService.generateTitlesSummaryInput(newsList);
            // 결합된 타이틀 문자열을 기반으로 뉴스 요약을 생성합니다.
            String summary = (String) apiRequestService.getNewsSummary(titlesSummaryInput).get("description");
            // 생성된 요약을 회사의 description 필드에 업데이트합니다.
            company.setDescription(summary);
            // 업데이트된 회사를 저장합니다.
            companyRepository.save(company);
            Thread.sleep(10000);
        }
    }

    private CompanyResponseDto mapToCompanyDto(Company company) {
        return new CompanyResponseDto(
                company.getId(),
                company.getName(),
                company.getDescription()
        );
    }

}
