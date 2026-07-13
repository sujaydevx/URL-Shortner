package com.sujay.urlshortener.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter urlCreatedCounter;
    private final Counter urlRedirectCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter expiredUrlCounter;
    private final Counter schedulerCleanupCounter;
    private final Counter databaseReadCounter;
    private final Counter databaseWriteCounter;


    public MetricsService(MeterRegistry meterRegistry) {

        this.urlCreatedCounter = Counter.builder("url_created_total")
                .description("Total URLs created")
                .register(meterRegistry);

        this.urlRedirectCounter = Counter.builder("url_redirect_total")
                .description("Total redirects")
                .register(meterRegistry);

        this.cacheHitCounter = Counter.builder("url_cache_hit_total")
                .description("Redis Cache Hits")
                .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("url_cache_miss_total")
                .description("Redis Cache Misses")
                .register(meterRegistry);

        this.expiredUrlCounter = Counter.builder("url_expired_total")
                .description("Expired URLs")
                .register(meterRegistry);

        this.schedulerCleanupCounter = Counter.builder("scheduler_cleanup_total")
                .description("Scheduler Cleanup Runs")
                .register(meterRegistry);

        this.databaseReadCounter = Counter.builder("url_db_read_total")
                .description("Total PostgreSQL Reads")
                .register(meterRegistry);

        this.databaseWriteCounter = Counter.builder("url_db_write_total")
                .description("Total PostgreSQL Writes")
                .register(meterRegistry);
    }

    public void incrementUrlCreated() {
        urlCreatedCounter.increment();
    }

    public void incrementUrlRedirect() {
        urlRedirectCounter.increment();
    }

    public void incrementCacheHit() {
        cacheHitCounter.increment();
    }

    public void incrementCacheMiss() {
        cacheMissCounter.increment();
    }

    public void incrementExpiredUrls() {
        expiredUrlCounter.increment();
    }

    public void incrementSchedulerCleanup() {
        schedulerCleanupCounter.increment();
    }

    public void incrementDatabaseRead() {
        databaseReadCounter.increment();
    }

    public void incrementDatabaseWrite() {
        databaseWriteCounter.increment();
    }

    public double getCacheHitCount() {
        return cacheHitCounter.count();
    }

    public double getCacheMissCount() {
        return cacheMissCounter.count();
    }
}