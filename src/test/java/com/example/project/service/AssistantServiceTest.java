package com.example.project.service;

import com.example.project.domain.response.AssistantResponse;
import com.example.project.exception.CustomServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.example.project.domain.constants.Constants.CODE_INTERPRETER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @InjectMocks
    private AssistantService service;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        service = new AssistantService(
                "Test Assistant",
                "gpt-4",
                "You are a helpful assistant.",
                webClient
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should create a new assistant.")
    void createAssistantTest() {
        String jsonResponse = "{\"id\": \"assistant_abc123\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        String result = service.createAssistant();

        assertEquals("assistant_abc123", result);
    }

    @Test
    @DisplayName("Should throw exception when assistant creation fails.")
    void createAssistantFailureTest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{}")
                .addHeader("Content-Type", "application/json"));

        CustomServiceException ex = assertThrows(CustomServiceException.class, () -> service.createAssistant());

        assertTrue(ex.getMessage().contains("Error creating a new assistant."));
    }

    @Test
    @DisplayName("Should upload and attach file.")
    void uploadAndAttachFileTest() {
        String fileId = "file_123";
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\": \"" + fileId + "\"}")
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                "sample content".getBytes(StandardCharsets.UTF_8)
        );

        String result = service.uploadAndAttachFile("assistant_abc", file);
        assertEquals(fileId, result);
    }

    @Test
    @DisplayName("Should create vector store.")
    void createVectorStoreTest() {
        String response = "{\"id\": \"vs_123\"}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(response)
                .addHeader("Content-Type", "application/json"));

        String result = service.createVectorStore("assistant_abc");

        assertEquals("vs_123", result);
    }

    @Test
    @DisplayName("Should attach vector store to assistant.")
    void attachVectorStoreToAssistantTest() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        assertDoesNotThrow(() ->
            service.attachVectorStoreToAssistant("assistant_abc", "vs_123"));
    }

    @Test
    @DisplayName("Should list assistant files.")
    void listFilesForAssistantTest() {
        String json = """
            {
              "tool_resources": {
                "code_interpreter": {
                  "file_ids": ["file_1", "file_2"]
                }
              }
            }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        JsonNode result = service.listFilesForAssistant("assistant_abc");

        assertTrue(result.has(CODE_INTERPRETER));
        assertTrue(result.get(CODE_INTERPRETER).get("file_ids").isArray());
    }

    @Test
    @DisplayName("Should get assistant by ID.")
    void getAssistantByIdTest() throws Exception {
        AssistantResponse assistantResponse = new AssistantResponse();
        assistantResponse.setId("asst_test123");
        assistantResponse.setName("AssistantOpenAI");
        assistantResponse.setModel("gpt-4");
        assistantResponse.setObject("assistant");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(assistantResponse);

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        AssistantResponse response = service.getAssistantById("asst_test123");

        assertNotNull(response);
        assertEquals("asst_test123", response.getId());
        assertEquals("AssistantOpenAI", response.getName());
        assertEquals("gpt-4", response.getModel());
    }

    @Test
    @DisplayName("Should add file to vector store.")
    void addFileToVectorStoreTest() {
        String vectorStoreId = "vs_123";
        String fileId = "file_123";

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        assertDoesNotThrow(() -> service.addFileToVectorStore(vectorStoreId, fileId));
    }

}
