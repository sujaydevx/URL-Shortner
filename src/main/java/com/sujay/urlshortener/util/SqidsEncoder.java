package com.sujay.urlshortener.util;

import org.springframework.stereotype.Component;
import org.sqids.Sqids;

import java.util.List;

@Component
public class SqidsEncoder {

    private final Sqids sqids;

    public SqidsEncoder() {

        this.sqids = Sqids.builder()
                .minLength(6)
                .build();
    }

    public String encode(Long id) {
        return sqids.encode(List.of(id));
    }

    public Long decode(String shortCode) {

        List<Long> decoded = sqids.decode(shortCode);

        if (decoded.isEmpty()) {
            throw new IllegalArgumentException("Invalid short code");
        }

        return decoded.get(0);
    }
}