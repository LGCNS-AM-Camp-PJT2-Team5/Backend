package com.team9.jobbotdari.scheduler;

import com.team9.jobbotdari.dto.NewsDto;
import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.repository.CompanyRepository;
import com.team9.jobbotdari.service.CompanyService;
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
@RequiredArgsConstructor
public class CompanySummaryScheduler {
    private final CompanyService companyService;

    /**
     * 매일 자정(00:00)과 정오(12:00)에 실행되는 스케줄러 메서드
     * 모든 회사에 대해 회사 이름을 검색어로 사용하여 뉴스 기사를 검색하고,
     * 해당 뉴스 기사 타이틀들을 기반으로 뉴스 요약을 생성하여 회사의 description 필드를 업데이트합니다.
     *
     * Cron 표현식 "0 0 0,12 * * *"는 매일 0시와 12시에 실행됨을 의미합니다.
     */
    @Scheduled(cron = "0 0 0,12 * * *")
    public void updateCompanyDescriptionScheduler() {
        companyService.updateCompanyDescription();

        // 업데이트 완료 로그 출력
        System.out.println("All Company descriptions updated at " + LocalDateTime.now());
    }
}