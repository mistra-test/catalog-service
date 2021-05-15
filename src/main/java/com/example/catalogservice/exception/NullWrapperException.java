package com.example.catalogservice.exception;

public class NullWrapperException extends RuntimeException {

    public NullWrapperException() {
        super("the returned wrapper is null");
    }
}
