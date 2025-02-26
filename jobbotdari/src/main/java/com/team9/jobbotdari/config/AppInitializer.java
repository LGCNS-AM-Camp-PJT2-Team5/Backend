package com.team9.jobbotdari.config;

import com.team9.jobbotdari.service.RecruitmentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppInitializer {
    private final SaraminApiConfig saraminApiConfig;
    private final RecruitmentService recruitmentService;

    @PostConstruct
    public void init() throws InterruptedException {
        saraminApiConfig.setCount(15);  // PostConstruct 실행 시 count = 15
        // 채용 정보 추가
        recruitmentService.addRecruitment();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterPostConstruct() {
        saraminApiConfig.setCount(30);
    }
}
