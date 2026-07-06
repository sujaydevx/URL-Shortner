package com.sujay.urlshortener.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    // getters ans setters are handled here.
    // this is what helps me to get the base-url from the yml file
    private String baseUrl;
}
