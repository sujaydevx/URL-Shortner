package com.sujay.urlshortener.service;

import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;
import com.sujay.urlshortener.dto.response.UrlStatsResponse;

public interface UrlService {

    ShortenUrlResponse shortenUrl(ShortenUrlRequest request);

    UrlStatsResponse getStats(String shortCode);

    String getOriginalUrl(String shortCode);

}