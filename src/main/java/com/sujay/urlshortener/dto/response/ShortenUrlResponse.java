package com.sujay.urlshortener.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ShortenUrlResponse(

        @Schema(example = "1024")
        Long id,

        @Schema(example = "https://chatgpt.com/c/6a49e6e5-ac0c-83ee-8841-8b573590db29")
        String originalUrl,

        @Schema(example = "bM91X")
        String shortCode,

        @Schema(example = "http://localhost:8080/bM91X")
        String shortUrl,

        LocalDateTime expiresAt,
        LocalDateTime createdAt

) {
}