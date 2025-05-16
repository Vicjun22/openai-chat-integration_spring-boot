package com.example.project.controller;

import com.example.project.domain.dto.AssistantDTO;
import com.example.project.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.project.domain.constants.Constants.WARNING_SAVE_RESPONSE;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/new")
    public ResponseEntity<String> newAssistant() {
        String id = assistantService.newAssistant();
        return ResponseEntity.ok(WARNING_SAVE_RESPONSE.concat(id));
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<AssistantDTO> getAssistant(@PathVariable String assistantId) {
        AssistantDTO dto = assistantService.getAssistantById(assistantId);
        return ResponseEntity.ok(dto);
    }
}
