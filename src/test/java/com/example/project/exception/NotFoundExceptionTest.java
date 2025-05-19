package com.example.project.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotFoundExceptionTest {

    @Test
    void testNotFoundParameterException() {
        String message = "Ocorreu um erro.";

        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
    }
}
