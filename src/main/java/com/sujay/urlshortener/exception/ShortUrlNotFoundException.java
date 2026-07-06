package com.sujay.urlshortener.exception;

public class ShortUrlNotFoundException extends RuntimeException{

    public ShortUrlNotFoundException(String shortCode){
        super("Short URL '" + shortCode + "' not found.");
    }

}
