package com.example.project.service;

import com.example.project.domain.response.AssistantResponse;
import com.example.project.exception.CustomServiceException;
import com.example.project.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.example.project.domain.constants.Constants.*;

@Service
public class AssistantService {

    private final String assistantName;
    private final String assistantModel;
    private final String assistantInstructions;
    private final WebClient webClient;

    private AssistantService(@Value("${openai.assistant.name}") String assistantName,
                             @Value("${openai.assistant.model}") String assistantModel,
                             @Value("${openai.assistant.instructions}") String assistantInstructions,
                             WebClient webClient) {
        this.assistantName = assistantName;
        this.assistantModel = assistantModel;
        this.assistantInstructions = assistantInstructions;
        this.webClient = webClient;
    }

    public String createAssistant() {
        Map<String, Object> payload = Map.of(
                ASSISTANT_NAME, assistantName,
                ASSISTANT_MODEL, assistantModel,
                ASSISTANT_INSTRUCTIONS, assistantInstructions,
                ASSISTANT_TOOLS, List.of(Map.of(TYPE, CODE_INTERPRETER))
        );

        JsonNode response = webClient.post()
                .uri(ASSISTANT_URI)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has(ID)) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Error creating a new assistant.");
        }
        return response.get(ID).asText();
    }

    public AssistantResponse getAssistantById(String assistantId) {
        return webClient.get()
                .uri(ASSISTANT_URI + "/" + assistantId)
                .retrieve()
                .bodyToMono(AssistantResponse.class)
                .block();
    }

    public String uploadAndAttachFile(String assistantId, MultipartFile file) {
        try {
            String fileId = uploadFile(file);
            attachFileToAssistant(assistantId, fileId);
            return fileId;
        } catch (IOException e) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Failed to process the file.");
        }
    }

    private String uploadFile(MultipartFile file) throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("purpose", "assistants");
        builder.part("file", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public @NonNull String getFilename() {
                        return file.getOriginalFilename();
                    }
                })
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "form-data; name=file; filename=" + file.getOriginalFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM);

        JsonNode response = webClient.post()
                .uri(FILES_URI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has(ID)) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Failed to upload file: missing ID in response.");
        }

        return response.get(ID).asText();
    }

    private void attachFileToAssistant(String assistantId, String fileId) {
        Map<String, Object> payload = Map.of(
                "tool_resources", Map.of(
                        "code_interpreter", Map.of(
                                "file_ids", List.of(fileId)
                        )
                )
        );

        webClient.post()
                .uri(ASSISTANT_URI + "/" + assistantId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public String createVectorStore(String assistantId) {
        Map<String, Object> payload = Map.of("name", "vs-for-" + assistantId);

        JsonNode response = webClient.post()
                .uri(VECTOR_STORES_URI)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has(ID)) {
            throw new CustomServiceException(HttpStatus.BAD_REQUEST, "Failed to create vector store: missing 'id' in response.");
        }

        return response.get(ID).asText();
    }


    public void addFileToVectorStore(String vectorStoreId, String fileId) {
        webClient.post()
                .uri(VECTOR_STORES_URI + "/" + vectorStoreId + FILES_URI)
                .bodyValue(Map.of("file_id", fileId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void attachVectorStoreToAssistant(String assistantId, String vectorStoreId) {
        Map<String, Object> body = Map.of(
                TOOL_RESOURCES, Map.of(
                        FILE_SEARCH, Map.of("vector_store_ids", List.of(vectorStoreId))
                )
        );

        webClient.post()
                .uri(ASSISTANT_URI + "/" + assistantId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public JsonNode listFilesForAssistant(String assistantId) {
        JsonNode response = webClient.get()
                .uri(ASSISTANT_URI + "/" + assistantId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has(TOOL_RESOURCES)) {
            throw new NotFoundException("No files attached to the assistant were found.");
        }

        return response.get(TOOL_RESOURCES);
    }
}
