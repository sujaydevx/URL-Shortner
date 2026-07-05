package com.sujay.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(

        @NotBlank(message = "URL cannot be blank")
        @URL(message = "Please provide a valid URL")
        String originalUrl

) {
}