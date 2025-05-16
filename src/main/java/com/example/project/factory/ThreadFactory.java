package com.example.project.factory;

import com.example.project.domain.dto.ThreadResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThreadFactory {

    public static ThreadResponse toResponse(String threadId, String runId, String answer) {
        ThreadResponse response = new ThreadResponse();
        response.setThreadId(threadId);
        response.setRunId(runId);
        response.setAnswer(answer);

        return response;
    }
}
