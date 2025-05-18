package com.example.project.service;

import com.example.project.domain.dto.AssistantDTO;
import com.example.project.exception.NotFoundException;
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

import java.io.IOException;
import java.util.List;
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

    public String createAssistant() {
        Map<String, Object> payload = Map.of(
                ASSISTANT_NAME, assistantName,
                ASSISTANT_MODEL, assistantModel,
                ASSISTANT_INSTRUCTIONS, assistantInstructions,
                ASSISTANT_TOOLS, List.of(Map.of(TYPE, FILE_SEARCH))
        );

        JsonNode response = webClient.post()
                .uri(ASSISTANT_URI)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("id"))
            throw new RuntimeException("Erro ao criar novo Assistant.");

        return response.get("id").asText();
    }

    public AssistantDTO getAssistantById(String assistantId) {
        return webClient.get()
                .uri(ASSISTANT_URI + "/" + assistantId)
                .retrieve()
                .bodyToMono(AssistantDTO.class)
                .block();
    }

    public String createVectorStore(String assistantId) {
        JsonNode response = webClient.post()
                .uri(VECTOR_STORES)
                .bodyValue(Map.of(NAME, "vs-for-" + assistantId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("id"))
            throw new RuntimeException("Erro ao criar Vector Store.");

        return response.get("id").asText();
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
                    .uri(FILES_URI)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (fileResponse == null || !fileResponse.has("id"))
                throw new RuntimeException("Erro no upload do arquivo.");

            String fileId = fileResponse.get("id").asText();

            Map<String, Object> attachPayload = Map.of(
                    TOOL_RESOURCES, Map.of(
                            CODE_INTERPRETER, Map.of(FILE_IDS, List.of(fileId))
                    )
            );

            webClient.post()
                    .uri(ASSISTANT_URI + "/" + assistantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(attachPayload)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return fileId;

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o arquivo.", e);
        }
    }

    public void addFileToVectorStore(String vectorStoreId, String fileId) {
        webClient.post()
                .uri(VECTOR_STORES + "/" + vectorStoreId + FILES_URI)
                .bodyValue(Map.of(FILE_ID, fileId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void attachVectorStoreToAssistant(String assistantId, String vectorStoreId) {
        Map<String, Object> body = Map.of(
                TOOL_RESOURCES, Map.of(
                        FILE_SEARCH, Map.of(VECTOR_STORE_IDS, List.of(vectorStoreId))
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

        if (response == null || !response.has(TOOL_RESOURCES))
            throw new NotFoundException("Nenhum arquivo encontrado para o Assistant.");

        return response.get(TOOL_RESOURCES);
    }
}
