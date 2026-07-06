package com.sujay.urlshortener.scheduler;

import com.sujay.urlshortener.service.CleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredUrlScheduler {

    private final CleanupService cleanupService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredUrls() {

        log.info("Starting expired URL cleanup job.");

        cleanupService.deleteExpiredUrls();

        log.info("Expired URL cleanup job completed.");
    }

}