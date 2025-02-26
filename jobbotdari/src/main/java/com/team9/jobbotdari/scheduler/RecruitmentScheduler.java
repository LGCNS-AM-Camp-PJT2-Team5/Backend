package com.team9.jobbotdari.scheduler;

import com.team9.jobbotdari.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecruitmentScheduler {
    private final RecruitmentService recruitmentService;

    /**
     * 매일 06:00와 18:00에 실행되는 스케줄러 메서드
     * 사람인 API를 활용하여 recruitment 테이블에 새로운 채용 정보를 추가 합니다.
     *
     * Cron 표현식 "0 0 6,18 * * *"는 매일 6시와 18시에 실행됨을 의미합니다.
     */
    @Async
    @Scheduled(cron = "0 0 6,18 * * *")
    public void addRecruitmentDataScheduler() {
        recruitmentService.addRecruitment();

        // 데이터 입력 완료 로그 출력
        System.out.println("All Recruitments insert at " + LocalDateTime.now());
    }
}
