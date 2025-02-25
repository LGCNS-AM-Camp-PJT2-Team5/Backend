package com.team9.jobbotdari.scheduler;

import com.team9.jobbotdari.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecruitmentScheduler {
    private final RecruitmentService recruitmentService;

    @Scheduled(cron = "0 30 0,12 * * *")
    public void addRecruitmentDataScheduler() {
        recruitmentService.addRecruitment();

        // 데이터 입력 완료 로그 출력
        System.out.println("All Recruitments insert at " + LocalDateTime.now());
    }
}
