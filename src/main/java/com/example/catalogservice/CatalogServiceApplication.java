package com.example.catalogservice;

import com.example.catalogservice.model.Movies;
import com.example.catalogservice.model.Ratings;
import com.example.catalogservice.security.JwtRequestFilter;

import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer;
import org.springframework.boot.context.ContextIdApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.web.client.RestTemplate;

// @EnableJms
@TypeHint(types = Ratings.class)
@TypeHint(types = Movies.class)
@TypeHint(types = JwtRequestFilter.class)
@TypeHint(types = ConfigurationWarningsApplicationContextInitializer.class)
@TypeHint(types = ContextIdApplicationContextInitializer.class)
@TypeHint(types = ParameterizedMessageFactory.class)
@NativeHint(trigger = RestTemplate.class, options = "--enable-url-protocols=http")
@SpringBootApplication
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Bean
    public RestTemplate movieTemplate() {
        return new RestTemplate();
    }
}
