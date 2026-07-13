package com.sujay.urlshortener.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.enums.ExpiryOption;
import com.sujay.urlshortener.metrics.MetricsService;
import com.sujay.urlshortener.repository.UrlRepository;
import com.sujay.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MetricsService metricsService;
    private static final int URL_COUNT = 15_000;

    private final UrlRepository urlRepository;
    private final UrlService urlService;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {

        if (urlRepository.count() > 0) {
            log.info("Database already contains data. Skipping data seeding.");
            return;
        }

        log.info("Generating {} URLs...", URL_COUNT);

        List<String> shortCodes = new ArrayList<>(URL_COUNT);

        for (int i = 1; i <= URL_COUNT; i++) {

            metricsService.incrementUrlCreated();
            String originalUrl =
                    "https://chatgpt.com/c/"
                            + UUID.randomUUID()
                            + "/conversation/"
                            + UUID.randomUUID()
                            + "?session="
                            + UUID.randomUUID()
                            + "&page="
                            + i
                            + "&user="
                            + UUID.randomUUID();

            ShortenUrlRequest request = new ShortenUrlRequest(
                    originalUrl,
                    ExpiryOption.ONE_DAY
            );

            ShortenUrlResponse response = urlService.shortenUrl(request);

            shortCodes.add(response.shortCode());

            if (i % 1000 == 0) {
                log.info("{} URLs generated...", i);
            }
        }

        File directory = new File("load-test");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(
                        new File(directory, "urls.json"),
                        shortCodes
                );

        log.info("=======================================");
        log.info("Data Seeding Completed Successfully");
        log.info("URLs Generated : {}", URL_COUNT);
        log.info("JSON File      : load-test/urls.json");
        log.info("=======================================");
    }
}