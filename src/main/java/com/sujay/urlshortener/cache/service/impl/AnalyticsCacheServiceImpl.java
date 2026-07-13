package com.sujay.urlshortener.cache.service.impl;

import com.sujay.urlshortener.cache.service.AnalyticsCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnalyticsCacheServiceImpl implements AnalyticsCacheService {

    private static final String CLICK_PREFIX = "analytics:click:";
    private static final String LAST_PREFIX = "analytics:last:";
    private static final String KEY_SET = "analytics:keys";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void incrementClick(String shortCode) {

        redisTemplate.opsForValue().increment(CLICK_PREFIX + shortCode);
        redisTemplate.opsForSet().add(KEY_SET, shortCode);

    }

    @Override
    public void updateLastAccessed(String shortCode) {

        redisTemplate.opsForValue().set(
                LAST_PREFIX + shortCode,
                LocalDateTime.now().toString()
        );

        redisTemplate.opsForSet().add(KEY_SET, shortCode);

    }

    @Override
    public Long getClickCount(String shortCode) {

        String value = redisTemplate.opsForValue()
                .get(CLICK_PREFIX + shortCode);

        return value == null ? 0L : Long.parseLong(value);

    }

    @Override
    public LocalDateTime getLastAccessed(String shortCode) {

        String value = redisTemplate.opsForValue()
                .get(LAST_PREFIX + shortCode);

        return value == null ? null : LocalDateTime.parse(value);

    }

    @Override
    public Set<String> getTrackedKeys() {

        return redisTemplate.opsForSet().members(KEY_SET);

    }

    @Override
    public void clear(String shortCode) {

        redisTemplate.delete(CLICK_PREFIX + shortCode);
        redisTemplate.delete(LAST_PREFIX + shortCode);
        redisTemplate.opsForSet().remove(KEY_SET, shortCode);

    }
}