package com.sujay.urlshortener.cache.service;

import java.time.LocalDateTime;
import java.util.Set;

public interface AnalyticsCacheService {

    void incrementClick(String shortCode);

    void updateLastAccessed(String shortCode);

    Long getClickCount(String shortCode);

    LocalDateTime getLastAccessed(String shortCode);

    Set<String> getTrackedKeys();

    void clear(String shortCode);
}