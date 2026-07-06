package com.sujay.urlshortener.repository;

import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.entity.Url;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    List<Url> findByExpiresAtIsNotNullAndExpiresAtBefore(LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime now);

}