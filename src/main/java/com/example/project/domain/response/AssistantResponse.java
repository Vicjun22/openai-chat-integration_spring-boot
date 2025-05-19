package com.example.project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantResponse {

    public String id;
    public String object;
    public String name;
    public String model;
}
