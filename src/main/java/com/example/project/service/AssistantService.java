package com.example.project.service;

import com.example.project.domain.dto.AssistantDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

import static com.example.project.domain.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class AssistantService {

    @Value("${openai.assistant.name}")
    private String assistantName;

    @Value("${openai.assistant.model}")
    private String assistantModel;

    @Value("${openai.assistant.instructions}")
    private String assistantInstructions;

    private final WebClient webClient;

    public String newAssistant() {
        Map<String, Object> payload = new HashMap<>();
        payload.put(ASSISTANT_NAME, assistantName);
        payload.put(ASSISTANT_MODEL, assistantModel);
        payload.put(ASSISTANT_INSTRUCTIONS, assistantInstructions);

        try {
            return webClient.post()
                    .uri(ASSISTANT_URL)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> json.get("id").asText())
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Response body: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    public AssistantDTO getAssistantById(String assistantId) {
        return webClient.get()
                .uri(ASSISTANT_URL + "/" + assistantId)
                .retrieve()
                .bodyToMono(AssistantDTO.class)
                .block();
    }
}
