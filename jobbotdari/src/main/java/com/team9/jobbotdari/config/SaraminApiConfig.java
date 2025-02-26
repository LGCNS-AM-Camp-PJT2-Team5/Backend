package com.team9.jobbotdari.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Getter
public class SaraminApiConfig {
    @Value("${saramin.api-url}")
    private String saraminApiUrl;


    @Value("${saramin.api-key}")
    private String saraminApiKey;

    private final AtomicInteger count = new AtomicInteger(30);

    public void setCount(int value) {
        this.count.set(value);
    }

    public Map<String, Object> getSaraminQueryParams() {
        return Map.of(
                "access-key", saraminApiKey,
                "job-category", "2",    // IT 직무
                "count", count.get(),               // 가져올 데이터 갯수
                "fields", "expiration-date",    // 데드라인 표시
                "sort", "rc"                   // ac: 지원자수 내림차순, rc: 조회수 내림차순

        );
    }
}
