package com.example.project.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class OpenAiConfigTest {

    @InjectMocks
    private OpenAiConfig config;

    @Test
    @DisplayName("Just coverage this method.")
    void justCoverageThisMethod() {
        assertDoesNotThrow(() -> config.openAiWebClient());
    }
}
