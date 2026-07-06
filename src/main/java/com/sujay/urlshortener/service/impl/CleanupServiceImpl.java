package com.sujay.urlshortener.service.impl;

import com.sujay.urlshortener.Config.AppProperties;
import com.sujay.urlshortener.entity.Url;
import com.sujay.urlshortener.repository.UrlRepository;
import com.sujay.urlshortener.service.CleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupServiceImpl implements CleanupService {

    private final UrlRepository urlRepository;
    private final AppProperties appProperties;

    @Override
    public void deleteExpiredUrls() {

        List<Url> expiredUrls =
                urlRepository.findByExpiresAtIsNotNullAndExpiresAtBefore(LocalDateTime.now());

        if (expiredUrls.isEmpty()) {
            log.info("No expired URLs found.");
            return;
        }

        for (Url url : expiredUrls) {

            log.info(
                    "Deleting expired URL. shortCode={}, shortUrl={}, originalUrl={}",
                    url.getShortCode(),
                    appProperties.getBaseUrl() + url.getShortCode(),
                    url.getOriginalUrl()
            );

            urlRepository.delete(url);
        }

        log.info("Deleted {} expired URLs.", expiredUrls.size());
    }

    @Override
    public void deleteExpiredUrl(Url url) {

        log.info("Deleting expired URL : {}", url.getShortCode());

        urlRepository.delete(url);

    }

}