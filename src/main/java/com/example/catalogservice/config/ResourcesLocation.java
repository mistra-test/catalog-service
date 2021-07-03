package com.example.catalogservice.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(value = "resources")
public class ResourcesLocation {

    private String movieResource;

    private String ratingResource;

    private String jwtResource;
}
