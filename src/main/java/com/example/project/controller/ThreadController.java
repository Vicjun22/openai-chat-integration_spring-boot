package com.example.project.controller;

import com.example.project.domain.dto.SendMessageRequest;
import com.example.project.domain.dto.ThreadResponse;
import com.example.project.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.project.factory.ThreadFactory.toResponse;

@RestController
@RequestMapping("/threads")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;

    @Value("${openai.assistant.id}")
    private String assistantId;

    @PostMapping("/chat")
    public ResponseEntity<ThreadResponse> startChat(@RequestBody SendMessageRequest request,
                                                    @RequestParam(required = false) String threadId) {

        if (threadId == null || threadId.isEmpty()) threadId = threadService.createThread();

        threadService.addMessageToThread(threadId, request.getMessage());
        String runId = threadService.runAssistant(threadId, assistantId);

        threadService.waitForRunCompletion(threadId, runId);
        String answer = threadService.getAssistantReply(threadId);

        return ResponseEntity.ok(toResponse(threadId, runId, answer));
    }
}
