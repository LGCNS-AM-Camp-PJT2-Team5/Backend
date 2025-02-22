package com.team9.jobbotdari.scheduler;

import com.team9.jobbotdari.dto.NewsDto;
import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.repository.CompanyRepository;
import com.team9.jobbotdari.service.NewsService;
import com.team9.jobbotdari.service.NewsSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Transactional
@RequiredArgsConstructor
public class CompanySummaryScheduler {
    // 회사 정보를 관리하는 Repository
    private final CompanyRepository companyRepository;

    // 뉴스 기사 검색 기능을 제공하는 서비스
    private final NewsService newsService;

    // 뉴스 요약 생성을 담당하는 서비스
    private final NewsSummaryService newsSummaryService;

    /**
     * 매일 자정(00:00)과 정오(12:00)에 실행되는 스케줄러 메서드
     * 모든 회사에 대해 회사 이름을 검색어로 사용하여 뉴스 기사를 검색하고,
     * 해당 뉴스 기사 타이틀들을 기반으로 뉴스 요약을 생성하여 회사의 description 필드를 업데이트합니다.
     *
     * Cron 표현식 "0 0 0,12 * * *"는 매일 0시와 12시에 실행됨을 의미합니다.
     */
    @Scheduled(cron = "0 0 0,12 * * *")
    public void updateCompanySummary() {
        // 1. 모든 회사 엔티티를 조회합니다.
        List<Company> companies = companyRepository.findAll();

        // 2. 각 회사를 순회하면서 뉴스 검색 및 요약 업데이트를 수행합니다.
        for (Company company : companies) {
            // 회사 이름을 검색어로 사용하여 뉴스 기사 리스트를 조회합니다.
            // 뉴스 검색어 변경 필요 시, 해당 부분 수정
            List<NewsDto> articles = newsService.searchNews(company.getName());
            // 뉴스 기사들의 타이틀에 번호를 붙여 하나의 문자열로 결합합니다.
            String titlesSummaryInput = newsService.generateTitlesSummaryInput(articles);
            // 결합된 타이틀 문자열을 기반으로 뉴스 요약을 생성합니다.
            Map<String, Object> summaryMap = newsSummaryService.getNewsSummary(titlesSummaryInput);
            String summary = (String) summaryMap.get("description");
            // 생성된 요약을 회사의 description 필드에 업데이트합니다.
            company.setDescription(summary);
            // 업데이트된 회사를 저장합니다.
            companyRepository.save(company);
        }

        // 업데이트 완료 로그 출력
        System.out.println("All Company descriptions updated at " + LocalDateTime.now());
    }
}