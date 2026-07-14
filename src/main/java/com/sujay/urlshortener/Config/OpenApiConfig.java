package com.sujay.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("URL Shortener API")
                                .version("1.0")
                                .description("""
                                        Production-ready URL Shortener built with
                                        Spring Boot, PostgreSQL, Redis,
                                        Prometheus, Grafana and Docker.
                                        """)
                                .contact(
                                        new Contact()
                                                .name("Sujay")
                                                .email("your-email@gmail.com")
                                                .url("https://github.com/yourusername")
                                )
                                .license(
                                        new License()
                                                .name("MIT")
                                )
                );
    }
}