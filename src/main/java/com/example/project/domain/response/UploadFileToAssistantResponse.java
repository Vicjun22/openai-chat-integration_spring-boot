package com.example.project.domain.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UploadFileToAssistantResponse {

    public String fileId;
    public String vectorStoreId;
}
