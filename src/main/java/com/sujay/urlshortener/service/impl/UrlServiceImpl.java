package com.sujay.urlshortener.service.impl;

import com.sujay.urlshortener.Config.AppProperties;
import com.sujay.urlshortener.cache.model.CachedUrl;
import com.sujay.urlshortener.cache.service.AnalyticsCacheService;
import com.sujay.urlshortener.cache.service.RedisCacheService;
import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.dto.response.UrlStatsResponse;
import com.sujay.urlshortener.entity.Url;
import com.sujay.urlshortener.exception.ShortUrlNotFoundException;
import com.sujay.urlshortener.exception.UrlExpiredException;
import com.sujay.urlshortener.metrics.MetricsService;
import com.sujay.urlshortener.repository.UrlRepository;
import com.sujay.urlshortener.service.CleanupService;
import com.sujay.urlshortener.service.UrlService;
import com.sujay.urlshortener.util.ExpiryCalculator;
import com.sujay.urlshortener.util.SqidsEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final AppProperties appProperties;
    private final CleanupService cleanupService;
    private final RedisCacheService redisCacheService;
    private final MetricsService metricsService;
    private final AnalyticsCacheService analyticsCacheService;
    private final SqidsEncoder sqidsEncoder;

    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        LocalDateTime expiresAt =
                ExpiryCalculator.calculateExpiry(request.expiryOption());

        Url url = Url.builder()
                .originalUrl(request.originalUrl())
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .lastAccessedAt(null)
                .expiresAt(expiresAt)
                .build();

        metricsService.incrementDatabaseWrite();
        Url savedUrl = urlRepository.save(url);

        String shortCode = sqidsEncoder.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        // this is not required to doit twice
        metricsService.incrementDatabaseWrite();
        savedUrl = urlRepository.save(savedUrl);

        // Cache immediately
        redisCacheService.save(
                shortCode,
                CachedUrl.builder()
                        .originalUrl(savedUrl.getOriginalUrl())
                        .expiresAt(savedUrl.getExpiresAt())
                        .build(),
                calculateTtl(savedUrl.getExpiresAt())
        );

        metricsService.incrementUrlCreated();

        String shortUrl = appProperties.getBaseUrl() + shortCode;
        System.out.println(appProperties.getBaseUrl());
        return new ShortenUrlResponse(
                savedUrl.getId(),
                savedUrl.getOriginalUrl(),
                shortCode,
                shortUrl,
                savedUrl.getExpiresAt(),
                savedUrl.getCreatedAt()
        );
    }

    @Override
    public UrlStatsResponse getStats(String shortCode) {
        metricsService.incrementDatabaseRead();
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(shortCode));

        return new UrlStatsResponse(
                url.getOriginalUrl(),
                url.getShortCode(),
                url.getClickCount(),
                url.getCreatedAt(),
                url.getLastAccessedAt(),
                url.getExpiresAt()
        );
    }

    @Override
    public String getOriginalUrl(String shortCode) {

        LocalDateTime now = LocalDateTime.now();

        Optional<CachedUrl> cachedUrlOptional = redisCacheService.get(shortCode);

        if (cachedUrlOptional.isPresent()) {

            metricsService.incrementCacheHit();

            CachedUrl cachedUrl = cachedUrlOptional.get();

            if (cachedUrl.getExpiresAt() != null &&
                    now.isAfter(cachedUrl.getExpiresAt())) {

                redisCacheService.delete(shortCode);

                throw new UrlExpiredException("This short URL has expired.");
            }

            analyticsCacheService.incrementClick(shortCode);
            analyticsCacheService.updateLastAccessed(shortCode);

            metricsService.incrementUrlRedirect();

            return cachedUrl.getOriginalUrl();
        }

        metricsService.incrementCacheMiss();
        metricsService.incrementDatabaseRead();
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(shortCode));

        if (url.getExpiresAt() != null &&
                now.isAfter(url.getExpiresAt())) {

            cleanupService.deleteExpiredUrl(url);

            metricsService.incrementExpiredUrls();

            throw new UrlExpiredException("This short URL has expired.");
        }

        CachedUrl cachedUrl = CachedUrl.builder()
                .originalUrl(url.getOriginalUrl())
                .expiresAt(url.getExpiresAt())
                .build();

        redisCacheService.save(
                shortCode,
                cachedUrl,
                calculateTtl(url.getExpiresAt())
        );

        analyticsCacheService.incrementClick(shortCode);
        analyticsCacheService.updateLastAccessed(shortCode);

        metricsService.incrementUrlRedirect();

        return url.getOriginalUrl();
    }

    private Duration calculateTtl(LocalDateTime expiresAt) {

        if (expiresAt == null) {
            return Duration.ofHours(24);
        }

        Duration ttl = Duration.between(LocalDateTime.now(), expiresAt);

        return ttl.isNegative() ? Duration.ZERO : ttl;
    }
}



//package com.sujay.urlshortener.service.impl;
//
//import com.sujay.urlshortener.Config.AppProperties;
//import com.sujay.urlshortener.cache.model.CachedUrl;
//import com.sujay.urlshortener.cache.service.AnalyticsCacheService;
//import com.sujay.urlshortener.cache.service.RedisCacheService;
//import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
//import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
//import com.sujay.urlshortener.dto.response.UrlStatsResponse;
//import com.sujay.urlshortener.entity.Url;
//import com.sujay.urlshortener.exception.ShortUrlNotFoundException;
//import com.sujay.urlshortener.exception.UrlExpiredException;
//import com.sujay.urlshortener.metrics.MetricsService;
//import com.sujay.urlshortener.repository.UrlRepository;
//import com.sujay.urlshortener.service.CleanupService;
//import com.sujay.urlshortener.service.UrlService;
//import com.sujay.urlshortener.util.Base62Encoder;
//import com.sujay.urlshortener.util.ExpiryCalculator;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class UrlServiceImpl implements UrlService {
//
//    private final UrlRepository urlRepository;
//    private final AppProperties appProperties;
//    private final CleanupService cleanupService;
//    private final RedisCacheService redisCacheService;
//    private final MetricsService metricsService;
//    private final AnalyticsCacheService analyticsCacheService;
//
//    @Override
//    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
//
//        LocalDateTime expiresAt =
//                ExpiryCalculator.calculateExpiry(request.expiryOption());
//
//        //build the object from the request dto
//        Url url = Url.builder()
//                .originalUrl(request.originalUrl())
//                .createdAt(LocalDateTime.now())
//                .clickCount(0L)
//                .lastAccessedAt(null)
//                .expiresAt(expiresAt)
//                .build();
//
//        //save this in the database
//        Url savedUrl = urlRepository.save(url);
//
//        // here we generate the shortcode for the original url
//        String shortCode = Base62Encoder.encode(savedUrl.getId());
//        savedUrl.setShortCode(shortCode);
//        savedUrl = urlRepository.save(savedUrl);
//        // this is for the metrics
//        metricsService.incrementUrlCreated();
//
//        // this is where i set the backend url where i will be hosting it
//        // for now its local
//        String shortUrl = appProperties.getBaseUrl() + shortCode;
//
//        // return the shortened code (heart logic is still left here)
//        return new ShortenUrlResponse(
//                savedUrl.getId(),
//                savedUrl.getOriginalUrl(),
//                shortCode,
//                shortUrl,
//                savedUrl.getExpiresAt(),
//                savedUrl.getCreatedAt()
//        );
//    }
//
//    // this will give me the details of the particular url
//    @Override
//    public UrlStatsResponse getStats(String shortCode) {
//
//        Url url = urlRepository.findByShortCode(shortCode)
//                .orElseThrow(() ->
//                        new ShortUrlNotFoundException(shortCode));
//
//        return new UrlStatsResponse(
//                url.getOriginalUrl(),
//                url.getShortCode(),
//                url.getClickCount(),
//                url.getCreatedAt(),
//                url.getLastAccessedAt(),
//                url.getExpiresAt()
//        );
//    }
//
//    //this is for retrieving the original url
//    @Override
//    public String getOriginalUrl(String shortCode) {
//
//        Optional<CachedUrl> cachedUrlOptional = redisCacheService.get(shortCode);
//
////        if (cachedUrlOptional.isPresent()) {
////            // if the id is present in the redis
////            metricsService.incrementCacheHit();
////            CachedUrl cachedUrl = cachedUrlOptional.get();
////
////            if (cachedUrl.getExpiresAt() != null &&
////                    LocalDateTime.now().isAfter(cachedUrl.getExpiresAt())) {
////
////                redisCacheService.delete(shortCode);
////
////                throw new UrlExpiredException("This short URL has expired.");
////            }
////            // this is if the cache misses it
////            metricsService.incrementCacheMiss();
////            Url url = urlRepository.findByShortCode(shortCode)
////                    .orElseThrow(() ->
////                            new ShortUrlNotFoundException(shortCode));
////
////            analyticsCacheService.incrementClick(shortCode);
////            analyticsCacheService.updateLastAccessed(shortCode);
////
////            // this tells me how many redirects happend
////            metricsService.incrementUrlRedirect();
////            return cachedUrl.getOriginalUrl();
////        }
//
//        if (cachedUrlOptional.isPresent()) {
//
//            metricsService.incrementCacheHit();
//
//            CachedUrl cachedUrl = cachedUrlOptional.get();
//
//            if (cachedUrl.getExpiresAt() != null &&
//                    LocalDateTime.now().isAfter(cachedUrl.getExpiresAt())) {
//
//                redisCacheService.delete(shortCode);
//                throw new UrlExpiredException("This short URL has expired.");
//            }
//
//            analyticsCacheService.incrementClick(shortCode);
//            analyticsCacheService.updateLastAccessed(shortCode);
//
//            metricsService.incrementUrlRedirect();
//
//            return cachedUrl.getOriginalUrl();
//        }
//        metricsService.incrementCacheMiss();
//
//
//        Url url = urlRepository.findByShortCode(shortCode)
//                .orElseThrow(() ->
//                        new ShortUrlNotFoundException(shortCode));
//
//        if (url.getExpiresAt() != null &&
//                LocalDateTime.now().isAfter(url.getExpiresAt())) {
//
//            cleanupService.deleteExpiredUrl(url);
//
//            // this is to track the number of urls that are expired
//            metricsService.incrementExpiredUrls();
//            throw new UrlExpiredException("This short URL has expired.");
//        }
//
//        CachedUrl cachedUrl = CachedUrl.builder()
//                .originalUrl(url.getOriginalUrl())
//                .expiresAt(url.getExpiresAt())
//                .build();
//
//        redisCacheService.save(
//                shortCode,
//                cachedUrl,
//                calculateTtl(url.getExpiresAt())
//        );
//
//        analyticsCacheService.incrementClick(shortCode);
//        analyticsCacheService.updateLastAccessed(shortCode);
//
//        metricsService.incrementUrlRedirect();
//        return url.getOriginalUrl();
//    }
//
//    private Duration calculateTtl(LocalDateTime expiresAt) {
//
//        if (expiresAt == null) {
//            return Duration.ofHours(24);
//        }
//
//        Duration ttl = Duration.between(LocalDateTime.now(), expiresAt);
//
//        return ttl.isNegative() ? Duration.ZERO : ttl;
//    }
//
//}