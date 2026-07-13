package com.sujay.urlshortener.scheduler;

import com.sujay.urlshortener.service.AnalyticsFlushService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsFlushScheduler {

    private final AnalyticsFlushService analyticsFlushService;

    @Scheduled(fixedRate = 60000)
    public void flushAnalytics() {

        analyticsFlushService.flush();

    }
}