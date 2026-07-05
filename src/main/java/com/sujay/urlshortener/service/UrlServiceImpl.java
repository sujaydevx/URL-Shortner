package com.sujay.urlshortener.service.impl;

import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.entity.Url;
import com.sujay.urlshortener.repository.UrlRepository;
import com.sujay.urlshortener.service.UrlService;
import com.sujay.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        //build the object from the request dto
        Url url = Url.builder()
                .originalUrl(request.originalUrl())
                .createdAt(LocalDateTime.now())
                .build();

        //save this in the database
        Url savedUrl = urlRepository.save(url);

        // here we generate the shortcode for the original url
        String shortCode = Base62Encoder.encode(savedUrl.getId());
        savedUrl.setShortCode(shortCode);
        savedUrl = urlRepository.save(savedUrl);

        // this is where i set the backend url where i will be hosting it
        // for now its local
        String shortUrl = "http://localhost:8080/" + shortCode;

        // return the shortened code (heart logic is still left here)
        return new ShortenUrlResponse(
                savedUrl.getId(),
                savedUrl.getOriginalUrl(),
                shortCode,
                shortUrl,
                savedUrl.getCreatedAt()
        );
    }
}