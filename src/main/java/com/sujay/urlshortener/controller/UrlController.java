package com.sujay.urlshortener.controller;

import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.dto.response.UrlStatsResponse;
import com.sujay.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "URL APIs", description = "Operations for shortening and redirecting URLs")
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @Operation(
            summary = "Create a Short URL",
            description = "Generates a Base62 encoded short URL from the provided original URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request
    ) {

        ShortenUrlResponse response = urlService.shortenUrl(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Redirect to Original URL",
            description = "Redirects the client to the original URL associated with the provided short code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect successful"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "410", description = "Short URL expired")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginal(
            @PathVariable String shortCode
    ){
        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @Operation(
            summary = "Retrieve URL Statistics",
            description = "Returns analytics and metadata for the specified short URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlStatsResponse> getStats(
            @PathVariable String shortCode
    ) {

        return ResponseEntity.ok(
                urlService.getStats(shortCode)
        );
    }



}
