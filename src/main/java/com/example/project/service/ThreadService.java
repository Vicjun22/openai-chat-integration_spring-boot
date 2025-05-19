package com.example.project.service;

import com.example.project.exception.CustomServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static com.example.project.domain.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class ThreadService {

    private final WebClient webClient;

    public String createThread() {
        return webClient.post()
                .uri(THREAD_URI)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get(ID).asText())
                .block();
    }

    public void addMessageToThread(String threadId, String content) {
        Map<String, Object> payload = Map.of(ROLE, USER, CONTENT, content);

        webClient.post()
                .uri(THREAD_URI + "/" + threadId + MESSAGES_URI)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public String runAssistant(String threadId, String assistantId) {
        Map<String, Object> payload = Map.of(ASSISTANT_ID, assistantId);

        return webClient.post()
                .uri(THREAD_URI + "/" + threadId + RUNS_URI)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get(ID).asText())
                .block();
    }

    public void waitForRunCompletion(String threadId, String runId) {
        String status;
        int attempts = 0;
        int maxAttempts = 30;

        while (attempts < maxAttempts) {
            JsonNode response = webClient.get()
                    .uri(THREAD_URI + "/" + threadId + RUNS_URI + "/" + runId)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            assert response != null;
            status = response.get(STATUS).asText();

            if (COMPLETED.equals(status)) return;
            else if (FAILED.equals(status)) {
                throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Run failed.");
            }

            attempts++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Thread interrupted");
            }
        }

        throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Timeout.");
    }

    public String getAssistantReply(String threadId) {
        JsonNode response = webClient.get()
                .uri(THREAD_URI + "/" + threadId + MESSAGES_URI)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        assert response != null;
        for (JsonNode message : response.get(DATA)) {
            if (ASSISTANT.equals(message.get(ROLE).asText())) {
                return message.get(CONTENT).get(0).get(TEXT).get(VALUE).asText();
            }
        }

        return NO_ANSWER_FOUND;
    }
}
