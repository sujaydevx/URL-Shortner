package com.sujay.urlshortener.service.impl;

import com.sujay.urlshortener.cache.service.AnalyticsCacheService;
import com.sujay.urlshortener.entity.Url;
import com.sujay.urlshortener.repository.UrlRepository;
import com.sujay.urlshortener.service.AnalyticsFlushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnalyticsFlushServiceImpl implements AnalyticsFlushService {

    private final AnalyticsCacheService analyticsCacheService;
    private final UrlRepository urlRepository;

    @Override
    public void flush() {

        Set<String> keys = analyticsCacheService.getTrackedKeys();

        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String shortCode : keys) {

            Url url = urlRepository.findByShortCode(shortCode)
                    .orElse(null);

            if (url == null) {
                analyticsCacheService.clear(shortCode);
                continue;
            }

            Long clicks = analyticsCacheService.getClickCount(shortCode);

            LocalDateTime lastAccessed =
                    analyticsCacheService.getLastAccessed(shortCode);

            url.setClickCount(url.getClickCount() + clicks);

            url.setLastAccessedAt(lastAccessed);

            urlRepository.save(url);

            analyticsCacheService.clear(shortCode);

        }
    }
}