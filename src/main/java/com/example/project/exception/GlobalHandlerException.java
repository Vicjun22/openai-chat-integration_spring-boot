package com.example.project.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalHandlerException {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handlerExceptionResolver(WebClientResponseException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAsString());
    }
}
