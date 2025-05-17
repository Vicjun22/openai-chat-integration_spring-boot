package com.example.project.service;

import com.example.project.domain.dto.AssistantDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public String uploadAndAttachFile(String assistantId, MultipartFile file) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part(PURPOSE, ASSISTANTS);
            builder.part(FILE, new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    })
                    .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=" + file.getOriginalFilename())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM);

            JsonNode fileResponse = webClient.post()
                    .uri(FILES_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            assert fileResponse != null;
            String fileId = fileResponse.get("id").asText();

            Map<String, Object> payload = Map.of(
                    TOOL_RESOURCES, Map.of(
                            CODE_INTERPRETER, Map.of(
                                    FILE_IDS, List.of(fileId)
                            )
                    )
            );

            webClient.post()
                    .uri(ASSISTANT_URL + "/" + assistantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return fileId;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode listFilesForAssistant(String assistantId) {
        return Objects.requireNonNull(webClient.get()
                        .uri(ASSISTANT_URL + "/" + assistantId)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block())
                .get(TOOL_RESOURCES);
    }

}
