package com.example.catalogservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientBuilder {

    @Bean
    public WebClient.Builder defaultWebClientBuilder() {
        return WebClient.builder();
    }
}
