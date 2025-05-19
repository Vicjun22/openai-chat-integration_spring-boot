package com.example.project.service;

import com.example.project.exception.CustomServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {

    @InjectMocks
    private ThreadService service;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        service = new ThreadService(webClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should create a new thread.")
    void createThreadTest() {
        String threadId = "thread_123";
        String jsonResponse = "{\"id\": \"thread_123\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        String result = service.createThread();
        assertEquals(threadId, result);
    }

    @Test
    @DisplayName("Should add a message to thread.")
    void addMessageToThreadTest() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        assertDoesNotThrow(() ->
                service.addMessageToThread("thread_123", "Hello!"));
    }

    @Test
    @DisplayName("Should run assistant and return runId.")
    void runAssistantTest() {
        String runId = "run_456";
        String jsonResponse = "{\"id\": \"run_456\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        String result = service.runAssistant("thread_123", "assistant_abc");
        assertEquals(runId, result);
    }

    @Test
    @DisplayName("Should wait until run is completed successfully.")
    void waitForRunCompletionTest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\": \"in_progress\"}")
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\": \"completed\"}")
                .addHeader("Content-Type", "application/json"));

        assertDoesNotThrow(() ->
                service.waitForRunCompletion("thread_123", "run_456"));
    }

    @Test
    @DisplayName("Should throw exception when run fails.")
    void waitForRunCompletionTestWhenRunFails() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\": \"failed\"}")
                .addHeader("Content-Type", "application/json"));

        CustomServiceException ex = assertThrows(CustomServiceException.class, () ->
                service.waitForRunCompletion("thread_123", "run_456"));

        assertEquals("Run failed.", ex.getMessage());
    }


    @Test
    @DisplayName("Should return assistant reply.")
    void getAssistantReplyTest() {
        String responseBody = """
        {
          "data": [
            {
              "role": "user",
              "content": []
            },
            {
              "role": "assistant",
              "content": [
                {
                  "text": {
                    "value": "Hello!"
                  }
                }
              ]
            }
          ]
        }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        String result = service.getAssistantReply("thread_123");
        assertEquals("Hello!", result);
    }

    @Test
    @DisplayName("Should return no answer when assistant reply is not found.")
    void getAssistantReplyTestWhenAssistantReplyIsNotFound() {
        String responseBody = """
        {
          "data": [
            {
              "role": "user",
              "content": []
            }
          ]
        }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        String result = service.getAssistantReply("thread_123");
        assertEquals("No answer found.", result);
    }
}
