package com.sujay.urlshortener.util;

public final class Base62Encoder {

    private static final String BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private Base62Encoder() {

    }

    public static String encode(long value) {

        if (value == 0) {
            return "0";
        }

        StringBuilder builder = new StringBuilder();

        while (value > 0) {

            builder.append(BASE62.charAt((int) (value % 62)));

            value /= 62;

        }

        return builder.reverse().toString();

    }

}