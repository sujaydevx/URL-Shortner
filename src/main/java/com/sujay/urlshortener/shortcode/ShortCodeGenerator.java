package com.sujay.urlshortener.shortcode;

public interface ShortCodeGenerator {

    String encode(long id);

    long decode(String shortCode);

}