package com.example.project.controller;

import com.example.project.domain.dto.AssistantDTO;
import com.example.project.service.AssistantService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.project.domain.constants.Constants.ID;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/new")
    public ResponseEntity<String> newAssistant() {
        String id = assistantService.newAssistant();
        return ResponseEntity.ok(ID.concat(id));
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<AssistantDTO> getAssistant(@PathVariable String assistantId) {
        AssistantDTO dto = assistantService.getAssistantById(assistantId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{assistantId}/upload")
    public ResponseEntity<String> uploadFileToAssistant(@PathVariable String assistantId,
                                                        @RequestParam("file") MultipartFile file) {
        String fileId = assistantService.uploadAndAttachFile(assistantId, file);
        return ResponseEntity.ok(ID + fileId);
    }

    @GetMapping("/{assistantId}/files")
    public ResponseEntity<JsonNode> listFilesForAssistant(@PathVariable String assistantId) {
        JsonNode files = assistantService.listFilesForAssistant(assistantId);
        return ResponseEntity.ok(files);
    }

}
