package com.sujay.urlshortener.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedUrl implements Serializable {

    // this is all we need to cache
    // we dont need the entire einformation to be stored
    private String originalUrl;

    private LocalDateTime expiresAt;

}