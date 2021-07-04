package com.example.catalogservice.security;

import com.example.catalogservice.config.ResourcesLocation;
import com.example.catalogservice.model.CustomAuthentication;

import lombok.AllArgsConstructor;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final WebClient.Builder webClientBuilder;
    private final ResourcesLocation resourcesLocation;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var authToken = authentication.getCredentials().toString();
        return fetchUserDetail(authToken);
    }

    private Mono<Authentication> fetchUserDetail(String jwt) {
        return webClientBuilder
                .build()
                .get()
                .uri(resourcesLocation.getJwtResource() + "/jwt/decode/" + jwt)
                .retrieve()
                .bodyToMono(User.class)
                .map(CustomAuthentication::new)
                .map(Authentication.class::cast);
    }
}
