package com.example.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static com.example.project.domain.constants.Constants.*;

@Configuration
public class OpenAiConfig {

    private final String apiKey;

    public OpenAiConfig(@Value("${openai.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl(OPENAI_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, BEARER + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(OPENAI_BETA, ASSISTANTS_V2)
                .build();
    }
}
