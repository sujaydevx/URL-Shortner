package com.sujay.urlshortener.service;

import com.sujay.urlshortener.entity.Url;

public interface CleanupService {

    void deleteExpiredUrls();

    void deleteExpiredUrl(Url url);

}