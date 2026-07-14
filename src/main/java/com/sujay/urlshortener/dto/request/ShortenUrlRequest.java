package com.sujay.urlshortener.dto.request;

import com.sujay.urlshortener.enums.ExpiryOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(

        @Schema(
                description = "Original URL to shorten",
                example = "https://chatgpt.com/c/6a49e6e5-ac0c-83ee-8841-8b573590db29"
        )
        @NotBlank(message = "URL cannot be blank")
        @URL(message = "Please provide a valid URL")
        String originalUrl,

        @Schema(
                description = "Expiration option for the URL",
                example = "SEVEN_DAYS"
        )
        ExpiryOption expiryOption

) {
}