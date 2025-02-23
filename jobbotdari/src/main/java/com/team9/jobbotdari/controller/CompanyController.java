package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.request.CompanyRequestDto;
import com.team9.jobbotdari.dto.response.CompanyResponseDto;
import com.team9.jobbotdari.dto.NewsDto;
import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.service.CompanyService;
import com.team9.jobbotdari.service.NewsService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCompanies() {
        List<CompanyResponseDto> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(Map.of("code", 200, "data", Map.of("companies", companies)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCompanyById(@PathVariable("id") Long id) {
        CompanyResponseDto company = companyService.getCompanyById(id);
        List<NewsDto> news = newsService.searchNews(company.getName());

        Map<String, Object> data = Map.of("company", company, "news", news);
        return ResponseEntity.ok(Map.of("code", 200, "data", data));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addCompany(@RequestBody CompanyRequestDto companyRequestDto) {
        Company createdCompany = companyService.addCompany(companyRequestDto);
        companyService.updateCompanyDescription(createdCompany);
        return ResponseEntity.ok(Map.of("code", 200, "data", Map.of("message", "기업 추가 완료")));
    }
}
