package com.example.project.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThreadResponse {

    public String threadId;
    public String runId;
    public String answer;
}
