package com.example.project.mock;

import com.example.project.domain.response.UploadFileToAssistantResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadFileToAssistantMock {

    public static UploadFileToAssistantResponse toResponseMock() {
        UploadFileToAssistantResponse response = new UploadFileToAssistantResponse();
        response.setFileId("file_abc123");
        response.setVectorStoreId("vs_xyz456");

        return response;
    }
}
