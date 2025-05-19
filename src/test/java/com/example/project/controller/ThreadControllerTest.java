package com.example.project.controller;

import com.example.project.domain.request.SendMessageRequest;
import com.example.project.service.ThreadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.example.project.mock.StartChatMock.toRequestMock;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ThreadControllerTest {

    @InjectMocks
    private ThreadController controller;

    @Mock
    private ThreadService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Should start a conversation thread between the user and an assistant.")
    void startChatTest() throws Exception {
        SendMessageRequest request = toRequestMock();

        request.setThreadId("");

        when(service.createThread()).thenReturn("asst_test123");
        doNothing().when(service).addMessageToThread(any(), any());
        when(service.runAssistant(any(), any())).thenReturn("runid_test123");
        doNothing().when(service).waitForRunCompletion(any(), any());
        when(service.getAssistantReply(any())).thenReturn("Answer...");

        mockMvc.perform(post("/threads/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).createThread();
        verify(service).addMessageToThread(any(), any());
        verify(service).runAssistant(any(), any());
        verify(service).waitForRunCompletion(any(), any());
        verify(service).getAssistantReply(any());
        verifyNoMoreInteractions(service);

        assertDoesNotThrow(() -> controller.startChat(toRequestMock()));
    }
}
