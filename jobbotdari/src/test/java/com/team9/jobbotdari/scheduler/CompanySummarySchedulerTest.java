package com.team9.jobbotdari.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class CompanySummarySchedulerTest {

    @Autowired
    private CompanySummaryScheduler companySummaryScheduler;

    @Test
    void testUpdateCompanySummary() {
        companySummaryScheduler.updateCompanySummary();
    }
}
