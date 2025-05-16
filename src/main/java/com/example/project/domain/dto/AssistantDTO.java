package com.example.project.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistantDTO {

    public String id;
    public String object;
    public String name;
    public String model;
}
