package com.example.project.factory;

import com.example.project.domain.response.UploadFileToAssistantResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadFileToAssistantFactory {

    public static UploadFileToAssistantResponse toResponse(String fileId, String vectorStoreId) {
        UploadFileToAssistantResponse response = new UploadFileToAssistantResponse();
        response.setFileId(fileId);
        response.setVectorStoreId(vectorStoreId);

        return response;
    }
}
