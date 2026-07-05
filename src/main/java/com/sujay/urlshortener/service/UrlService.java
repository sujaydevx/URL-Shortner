package com.sujay.urlshortener.service;

import com.sujay.urlshortener.dto.request.ShortenUrlRequest;
import com.sujay.urlshortener.dto.response.ShortenUrlResponse;

public interface UrlService {

    ShortenUrlResponse shortenUrl(ShortenUrlRequest request);

}