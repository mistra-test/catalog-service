package com.example.catalogservice.exception;

import org.springframework.security.core.context.SecurityContextHolder;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String error) {
        super(
                String.format(
                        "for user with id %s, %s",
                        SecurityContextHolder.getContext().getAuthentication().getName(), error));
    }
}
