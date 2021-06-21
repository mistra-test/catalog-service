package com.example.catalogservice.security;

import com.example.catalogservice.model.CustomUser;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
class CustomAuthority implements GrantedAuthority {
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}

@Log4j2
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired RestTemplate restTemplate;

    @Value("${com.example.jwt-resource.location}")
    private String jwtResourceEndpoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Consumer<UserDetails> authenticate =
                user -> {
                    var usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext()
                            .setAuthentication(usernamePasswordAuthenticationToken);
                    try {
                        chain.doFilter(request, response);
                    } catch (IOException e) {
                        log.error("internal error while authenticating");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    } catch (ServletException e) {
                        log.error("unknown internal error while authenticating");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                };

        var jwt = authorizationHeader.substring(7);
        fetchUserDetails(jwt)
                .ifPresentOrElse(
                        authenticate,
                        () -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED));
    }

    private Optional<UserDetails> fetchUserDetails(String jwt) {
        try {
            UserDetails user =
                    new CustomUser(
                            restTemplate.getForObject(
                                    jwtResourceEndpoint + "/jwt/decode/" + jwt, User.class));
            return Optional.of(user);
        } catch (RestClientException e) {
            log.error("jwt-resource not reponding");
            return Optional.empty();
        }
    }
}
