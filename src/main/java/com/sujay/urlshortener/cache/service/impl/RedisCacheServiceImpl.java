package com.sujay.urlshortener.cache.service.impl;

import com.sujay.urlshortener.cache.model.CachedUrl;
import com.sujay.urlshortener.cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final String KEY_PREFIX = "url:";

    private final RedisTemplate<String, CachedUrl> redisTemplate;

    @Override
    public Optional<CachedUrl> get(String shortCode) {

        System.out.println("Checking Redis for : " + shortCode);

        CachedUrl value = redisTemplate.opsForValue()
                .get(KEY_PREFIX + shortCode);

        if (value == null) {
            System.out.println("Cache MISS");
        } else {
            System.out.println("Cache HIT");
        }

        return Optional.ofNullable(value);
    }

    @Override
    public void save(String shortCode,
                     CachedUrl cachedUrl,
                     Duration ttl) {

        redisTemplate.opsForValue().set(
                KEY_PREFIX + shortCode,
                cachedUrl,
                ttl
        );

    }

    @Override
    public void delete(String shortCode) {

        redisTemplate.delete(KEY_PREFIX + shortCode);

    }

}