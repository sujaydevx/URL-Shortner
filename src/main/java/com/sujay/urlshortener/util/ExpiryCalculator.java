package com.sujay.urlshortener.util;

import com.sujay.urlshortener.enums.ExpiryOption;

import java.time.LocalDateTime;

public class ExpiryCalculator {

    private ExpiryCalculator() {
    }

    public static LocalDateTime calculateExpiry(ExpiryOption option) {

        if (option == null || option == ExpiryOption.NONE) {
            return null;
        }

        return switch (option) {

            case ONE_DAY -> LocalDateTime.now().plusDays(1);

            case THREE_DAYS -> LocalDateTime.now().plusDays(3);

            case SEVEN_DAYS -> LocalDateTime.now().plusDays(7);

            case THIRTY_DAYS -> LocalDateTime.now().plusDays(30);

            default -> null;
        };
    }

}
