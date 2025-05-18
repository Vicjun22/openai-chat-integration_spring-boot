package com.example.project.controller;

import com.example.project.domain.dto.AssistantDTO;
import com.example.project.domain.dto.UploadFileToAssistantResponse;
import com.example.project.service.AssistantService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.project.factory.UploadFileToAssistantFactory.toResponse;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/new")
    public ResponseEntity<String> createAssistant() {
        String id = assistantService.createAssistant();
        return ResponseEntity.ok("ID: " + id);
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<AssistantDTO> getAssistant(@PathVariable String assistantId) {
        return ResponseEntity.ok(assistantService.getAssistantById(assistantId));
    }

    @PostMapping("/{assistantId}/upload")
    public ResponseEntity<UploadFileToAssistantResponse> uploadFile(@PathVariable String assistantId,
                                                                    @RequestParam("file") MultipartFile file) {
        String fileId = assistantService.uploadAndAttachFile(assistantId, file);
        String vectorStoreId = assistantService.createVectorStore(assistantId);
        assistantService.addFileToVectorStore(vectorStoreId, fileId);
        assistantService.attachVectorStoreToAssistant(assistantId, vectorStoreId);

        return ResponseEntity.ok(toResponse(fileId, vectorStoreId));
    }

    @GetMapping("/{assistantId}/files")
    public ResponseEntity<JsonNode> listFiles(@PathVariable String assistantId) {
        return ResponseEntity.ok(assistantService.listFilesForAssistant(assistantId));
    }
}
