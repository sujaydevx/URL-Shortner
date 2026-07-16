package com.sujay.urlshortener.shortcode;

import org.springframework.stereotype.Component;
import org.sqids.Sqids;

import java.util.List;

@Component
public class SqidsShortCodeGenerator implements ShortCodeGenerator {

    private final Sqids sqids;

    public SqidsShortCodeGenerator() {

        this.sqids = Sqids.builder()
                .minLength(6)
                .build();

    }

    @Override
    public String encode(long id) {

        return sqids.encode(List.of(id));

    }

    @Override
    public long decode(String shortCode) {

        List<Long> numbers = sqids.decode(shortCode);

        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("Invalid short code.");
        }

        return numbers.getFirst();

    }
}