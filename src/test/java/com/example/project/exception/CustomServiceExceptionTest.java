package com.example.project.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomServiceExceptionTest {

    @Test
    @DisplayName("Should execute the error.")
    void testCustomServiceException() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Error";

        CustomServiceException exception = new CustomServiceException(status, message);

        assertEquals(status, exception.getStatus());
        assertEquals(message, exception.getMessage());
    }
}
