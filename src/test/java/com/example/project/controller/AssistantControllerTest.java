package com.example.project.controller;

import com.example.project.domain.response.AssistantResponse;
import com.example.project.domain.response.UploadFileToAssistantResponse;
import com.example.project.service.AssistantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.example.project.mock.AssistantMock.getAssistantDTOMock;
import static com.example.project.mock.UploadFileToAssistantMock.toResponseMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AssistantControllerTest {

    @InjectMocks
    private AssistantController controller;

    @Mock
    private AssistantService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Should create a new assistant.")
    void createAssistantTest() throws Exception {
        when(service.createAssistant()).thenReturn("asst_test123");

        mockMvc.perform(post("/assistant/new"))
                .andExpect(status().isCreated());

        verifyNoMoreInteractions(service);

        ResponseEntity<String> response = controller.createAssistant();
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    @DisplayName("Should return the details of an existing assistant by ID")
    void getAssistantTest() throws Exception {
        String assistantId = "asst_test123";
        when(service.getAssistantById(assistantId)).thenReturn(getAssistantDTOMock());

        mockMvc.perform(get("/assistant/{assistantId}", assistantId))
                .andExpect(status().isOk());

        verifyNoMoreInteractions(service);

        ResponseEntity<AssistantResponse> response = controller.getAssistant(assistantId);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertEquals("gpt-4", response.getBody().model);
        assertEquals("assistant", response.getBody().object);
        assertEquals("AssistantOpenAI", response.getBody().name);
    }

    @Test
    @DisplayName("Should upload file and attach to assistant with vector store")
    void shouldUploadFileAndAttachToAssistant() throws Exception {
        String assistantId = "asst_test123";
        UploadFileToAssistantResponse uploadFileResponse = toResponseMock();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.json",
                "application/json",
                "{\"key\":\"value\"}".getBytes()
        );

        when(service.uploadAndAttachFile(assistantId, file)).thenReturn(uploadFileResponse.getFileId());
        when(service.createVectorStore(assistantId)).thenReturn(uploadFileResponse.getVectorStoreId());

        mockMvc.perform(multipart("/assistant/{assistantId}/upload", assistantId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").value(uploadFileResponse.getFileId()))
                .andExpect(jsonPath("$.vectorStoreId").value(uploadFileResponse.getVectorStoreId()));

        verify(service).uploadAndAttachFile(assistantId, file);
        verify(service).createVectorStore(assistantId);
        verify(service).addFileToVectorStore(uploadFileResponse.getVectorStoreId(), uploadFileResponse.getFileId());
        verify(service).attachVectorStoreToAssistant(assistantId, uploadFileResponse.getVectorStoreId());
        verifyNoMoreInteractions(service);

        assertDoesNotThrow(() -> controller.uploadFile(assistantId, file));
    }

    @Test
    @DisplayName("Should list all files attached to the assistant")
    void listFilesTest() throws Exception {
        String assistantId = "asst_test123";

        String json = """
        {
            "file_search": {
                "vector_store_ids": [
                    "vs_xyz456"
                ]
            },
            "code_interpreter": {
                "file_ids": [
                    "file_abc123"
                ]
            }
        }
        """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedJsonNode = objectMapper.readTree(json);

        when(service.listFilesForAssistant(assistantId)).thenReturn(expectedJsonNode);

        mockMvc.perform(get("/assistant/{assistantId}/files", assistantId))
                .andExpect(status().isOk());

        verifyNoMoreInteractions(service);

        ResponseEntity<JsonNode> response = controller.listFiles(assistantId);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }
}
