package com.example.project.controller;

import com.example.project.domain.dto.SendMessageRequest;
import com.example.project.domain.dto.ThreadResponse;
import com.example.project.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.project.factory.ThreadFactory.toResponse;

@RestController
@RequestMapping("/threads")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;

    @PostMapping("/chat")
    public ResponseEntity<ThreadResponse> startChat(@RequestBody SendMessageRequest request) {

        if (request.getThreadId() == null || request.getThreadId().isEmpty()) request.setThreadId(threadService.createThread());

        threadService.addMessageToThread(request.getThreadId(), request.getMessage());

        String runId = threadService.runAssistant(request.getThreadId(), request.getAssistantId(), request.getFieldId());

        threadService.waitForRunCompletion(request.getThreadId(), runId);
        String answer = threadService.getAssistantReply(request.getThreadId());

        return ResponseEntity.ok(toResponse(request.getThreadId(), runId, answer));
    }

}
