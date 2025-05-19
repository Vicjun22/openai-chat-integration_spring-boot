package com.example.project.mock;

import com.example.project.domain.response.AssistantResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssistantMock {

    public static AssistantResponse getAssistantDTOMock() {
        AssistantResponse response = new AssistantResponse();
        response.setId("asst_test123");
        response.setName("AssistantOpenAI");
        response.setModel("gpt-4");
        response.setObject("assistant");

        return response;
    }
}
