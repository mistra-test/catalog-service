package com.example.catalogservice.security;

import com.example.catalogservice.model.CustomUser;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired RestTemplate restTemplate;

    @Autowired CurrentUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        UserDetails user = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);

                user =
                        new CustomUser(
                                restTemplate.getForObject(
                                        "http://jwt-resource/jwt/decode/" + jwt, User.class));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception e) {
            // TODO: da gestire come Dio comanda
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        chain.doFilter(request, response);
    }

    // private UserDetails
}
