package com.example.project.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotFoundExceptionTest {

    @Test
    @DisplayName("Should execute the error.")
    void testNotFoundException() {
        String message = "Error.";

        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
    }
}
