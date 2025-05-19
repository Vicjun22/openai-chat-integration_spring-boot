package com.example.project.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Constants {

    public static final String OPENAI_URL = "https://api.openai.com/v1";

    public static final String ASSISTANT_URI = "/assistants";
    public static final String THREAD_URI = "/threads";
    public static final String MESSAGES_URI = "/messages";
    public static final String RUNS_URI = "/runs";
    public static final String FILES_URI = "/files";
    public static final String VECTOR_STORES_URI = "/vector_stores";

    public static final String BEARER = "Bearer ";
    public static final String OPENAI_BETA = "OpenAI-Beta";
    public static final String ASSISTANTS_V2 = "assistants=v2";

    public static final String ASSISTANT_NAME = "name";
    public static final String ASSISTANT_MODEL = "model";
    public static final String ASSISTANT_INSTRUCTIONS = "instructions";
    public static final String ASSISTANT_ID = "assistant_id";
    public static final String ASSISTANT_TOOLS = "tools";

    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String DATA = "data";
    public static final String TYPE = "type";
    public static final String ROLE = "role";
    public static final String USER = "user";
    public static final String VALUE = "value";
    public static final String FAILED = "failed";
    public static final String STATUS = "status";
    public static final String CONTENT = "content";
    public static final String COMPLETED = "completed";
    public static final String ASSISTANT = "assistant";
    public static final String FILE_SEARCH = "file_search";
    public static final String TOOL_RESOURCES = "tool_resources";
    public static final String CODE_INTERPRETER = "code_interpreter";

    public static final String NO_ANSWER_FOUND = "No answer found";

}
