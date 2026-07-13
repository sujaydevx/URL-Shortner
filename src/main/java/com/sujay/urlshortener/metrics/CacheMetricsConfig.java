package com.sujay.urlshortener.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheMetricsConfig {

    public CacheMetricsConfig(
            MeterRegistry meterRegistry,
            MetricsService metricsService) {

        Gauge.builder(
                        "url_cache_hit_ratio",
                        () -> {
                            double hits = metricsService.getCacheHitCount();
                            double misses = metricsService.getCacheMissCount();

                            if (hits + misses == 0) {
                                return 0;
                            }

                            return hits / (hits + misses);
                        })
                .description("Redis Cache Hit Ratio")
                .register(meterRegistry);
    }
}