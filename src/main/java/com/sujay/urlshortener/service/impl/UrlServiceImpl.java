package com.sujay.urlshortener.service.impl;

import com.sujay.urlshortener.Config.AppProperties;
import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.dto.response.UrlStatsResponse;
import com.sujay.urlshortener.entity.Url;
import com.sujay.urlshortener.exception.ShortUrlNotFoundException;
import com.sujay.urlshortener.exception.UrlExpiredException;
import com.sujay.urlshortener.repository.UrlRepository;
import com.sujay.urlshortener.service.CleanupService;
import com.sujay.urlshortener.service.UrlService;
import com.sujay.urlshortener.util.Base62Encoder;
import com.sujay.urlshortener.util.ExpiryCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final AppProperties appProperties;
    private final CleanupService cleanupService;

    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        LocalDateTime expiresAt =
                ExpiryCalculator.calculateExpiry(request.expiryOption());

        //build the object from the request dto
        Url url = Url.builder()
                .originalUrl(request.originalUrl())
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .lastAccessedAt(null)
                .expiresAt(expiresAt)
                .build();

        //save this in the database
        Url savedUrl = urlRepository.save(url);

        // here we generate the shortcode for the original url
        String shortCode = Base62Encoder.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        savedUrl = urlRepository.save(savedUrl);

        // this is where i set the backend url where i will be hosting it
        // for now its local
        String shortUrl = appProperties.getBaseUrl() + shortCode;

        // return the shortened code (heart logic is still left here)
        return new ShortenUrlResponse(
                savedUrl.getId(),
                savedUrl.getOriginalUrl(),
                shortCode,
                shortUrl,
                savedUrl.getExpiresAt(),
                savedUrl.getCreatedAt()
        );
    }

    // this will give me the details of the particular url
    @Override
    public UrlStatsResponse getStats(String shortCode) {

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

    //this is for retrieving the original url
    @Override
    public String getOriginalUrl(String shortCode){


        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(shortCode));

        if (url.getExpiresAt() != null &&
                LocalDateTime.now().isAfter(url.getExpiresAt())) {

            cleanupService.deleteExpiredUrl(url);

            throw new UrlExpiredException("This short URL has expired.");
        }

        url.setClickCount(url.getClickCount() + 1);
        url.setLastAccessedAt(LocalDateTime.now());
        urlRepository.save(url);

        return url.getOriginalUrl();
    }
}