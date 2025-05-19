package com.example.project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomServiceExceptionTest {

    @Test
    void testCustomServiceException() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Ocorreu um erro.";

        CustomServiceException exception = new CustomServiceException(status, message);

        assertEquals(status, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }
}
