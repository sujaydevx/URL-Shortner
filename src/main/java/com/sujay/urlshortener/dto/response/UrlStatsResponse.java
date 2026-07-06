package com.sujay.urlshortener.dto.response;

import java.time.LocalDateTime;

public record UrlStatsResponse(

        String originalUrl,
        String shortCode,
        Long clickCount,
        LocalDateTime createdAt,
        LocalDateTime lastAccessedAt,
        LocalDateTime expiresAt

) {
}