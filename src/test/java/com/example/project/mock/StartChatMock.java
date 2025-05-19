package com.example.project.mock;

import com.example.project.domain.request.SendMessageRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartChatMock {

    public static SendMessageRequest toRequestMock() {
        SendMessageRequest request = new SendMessageRequest();
        request.setAssistantId("asst_test123");
        request.setThreadId("thread_test123");
        request.setMessage("The file contains a list of user transactions from January 2024...");

        return request;
    }
}
