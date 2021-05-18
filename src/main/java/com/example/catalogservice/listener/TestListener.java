package com.example.catalogservice.listener;

import lombok.extern.log4j.Log4j2;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TestListener {

    @JmsListener(destination = "test")
    public void testMethod(String message) {
        log.info(message);
    }
}
