package com.example.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomServiceException extends RuntimeException {
    private final HttpStatus status;

    public CustomServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
