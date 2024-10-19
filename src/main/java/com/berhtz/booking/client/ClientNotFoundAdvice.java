package com.berhtz.booking.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ClientNotFoundAdvice {
    
    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String clientNotFound(ClientNotFoundException ex) {
        return ex.getMessage();
    }
}
