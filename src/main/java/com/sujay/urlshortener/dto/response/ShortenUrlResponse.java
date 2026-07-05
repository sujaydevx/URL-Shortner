package com.sujay.urlshortener.dto.response;

import java.time.LocalDateTime;

public record ShortenUrlResponse(

        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        LocalDateTime createdAt

) {
}