package com.example.project.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalHandlerException globalHandlerException;

    @Test
    @DisplayName("Should handle CustomServiceException correctly")
    void shouldHandleCustomServiceException() {
        CustomServiceException exception = new CustomServiceException(HttpStatus.BAD_REQUEST, "Error");
        ResponseEntity<Object> response = globalHandlerException.handlerExceptionResolver(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle NotFoundException correctly")
    void shouldHandleNotFoundParameterException() {
        NotFoundException exception = new NotFoundException("Error");
        ResponseEntity<Object> response = globalHandlerException.handlerExceptionResolver(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle WebClientResponseException correctly")
    void shouldHandleWebClientResponseException() {
        WebClientResponseException exception = new WebClientResponseException(400, "Error", null, null, mock(Charset.class));
        ResponseEntity<Object> response = globalHandlerException.handlerExceptionResolver(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle RuntimeException correctly")
    void shouldHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Error");
        ResponseEntity<Object> response = globalHandlerException.handlerExceptionResolver(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
