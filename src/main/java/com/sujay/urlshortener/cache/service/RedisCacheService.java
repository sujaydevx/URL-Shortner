package com.sujay.urlshortener.cache.service;

import com.sujay.urlshortener.cache.model.CachedUrl;

import java.time.Duration;
import java.util.Optional;

public interface RedisCacheService {
    // all thee operations are mentioned here
    Optional<CachedUrl> get(String shortCode);

    void save(String shortCode,
              CachedUrl cachedUrl,
              Duration ttl);

    void delete(String shortCode);

}