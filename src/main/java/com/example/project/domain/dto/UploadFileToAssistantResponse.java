package com.example.project.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UploadFileToAssistantResponse {

    public String fileId;
    public String vectorStoreId;
}
