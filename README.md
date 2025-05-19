# OpenAI: Chat integration with Spring Boot

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-blue)

This repository demonstrates how to integrate OpenAI chat functionality into a Spring Boot project.

### 1. Create a Spring Boot Project

First, create a new Spring Boot project using your preferred method (e.g., [Spring Initializr](https://start.spring.io/) or an IDE like IntelliJ IDEA or Eclipse). Make sure your project is set up with Maven.

### 2. Add dependencies to the `pom.xml` file:

```xml
<!--	Spring Web	-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!--	Lombok	-->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!--	WebFlux	-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<!--	JSON parsing	-->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### 3. Define your secrets in `application.yml`

```yaml
openai:
  api-key: ${OPENAI_TOKEN}
  assistant:
    name: ${ASSISTANT_NAME}
    model: ${ASSISTANT_MODEL}
    instructions: ${ASSISTANT_INSTRUCTIONS}
```

### 4. Create an OpenAI configuration class:

```java
@Configuration
public class OpenAiConfig {

    private final String apiKey;

    public OpenAiConfig(@Value("${openai.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("OpenAI-Beta", "assistants=v2")
                .build();
    }
}
```

<hr>

Now that we’ve completed the configuration, we can move on to explaining the available endpoints.

## ⚙️ Assistant Controller

`POST /assistant/new`

Sends a request to create a new assistant using the configuration defined in `application.yml`.
```curl
curl --location --request POST 'http://localhost:8080/assistant/new'
```

**Response:**
```text
ID: asst_123abc456def...
```

<br>

`GET /assistant/{assistantId}`

Returns the details of an existing assistant by ID.
```curl
curl --location 'http://localhost:8080/assistant/asst_js0TkCNlOgIxqt6ir3gGmt2O'
```

<br>

`POST /assistant/{assistantId}/upload`

Uploads a file and attaches it to the specified assistant.<br>
The file is also stored in a vector store and linked to the assistant for retrieval and search purposes.
```curl
curl --location 'http://localhost:8080/assistant/asst_js0TkCNlOgIxqt6ir3gGmt2O/upload' \
--form 'file=@"C:/Users/Documents/data.json"'
```

**Response:**
```json
{
  "fileId": "file_abc123...",
  "vectorStoreId": "vs_xyz456..."
}
```

<br>

`GET /assistant/{assistantId}/files`

Lists all files attached to the assistant.
```curl
curl --location 'http://localhost:8080/assistant/asst_js0TkCNlOgIxqt6ir3gGmt2O/files' \
--header 'OpenAI-Beta: assistants=v2'
```

## ⚙️ Thread Controller

`POST /threads/chat`

Starts or continues a conversation thread between the user and an assistant.<br>
**If a `threadId` is not provided, a new thread is created.**<br>
The message is added to the thread, the assistant processes it, and the response is returned after execution is complete.
```curl
curl --location 'http://localhost:8080/threads/chat' \
--data '{
    "assistantId": "asst_js0TkCNlOgIxqt6ir3gGmt2O",
    "threadId": "thread_rzSQwwxikoALUUlSQ35OIfcM",
    "message": "What are the contents of the uploaded file?"
}'
```

**Request:**
```json
{
  "assistantId": "asst_js0TkCNlOgIxqt6ir3gGmt2O",
  "threadId": "",  // Optional: leave empty to create a new thread
  "message": "What are the contents of the uploaded file?"
}
```

**Response:**
```json
{
  "threadId": "thread_xyz789...",
  "runId": "run_abc456...",
  "answer": "The file contains a list of user transactions from January 2024..."
}
```

**Behavior:**
- If `threadId` is omitted or blank, a new thread is automatically created.
- The message is sent to the assistant, which processes it.
- The system waits for the assistant’s response before replying to the client.

<hr>

## 📁 Project Resources
The `resources/assets` directory contains the following files:

- `data.json`:<br>
An example input file containing structured data (in Brazilian Portuguese).

- `PROJECT_OPENAI.postman_collection.json`:<br>
A Postman collection with predefined requests to test the API endpoints.

- `instructions.txt`:<br>
A file with prompt instructions for the Assistant (written in Brazilian Portuguese).

---

## 🔗 Useful Links

- [Spring Initializr](https://start.spring.io/) - Quickly bootstrap a Spring Boot project.
- [OpenAI Platform](https://platform.openai.com/docs/overview) - Learn more about OpenAI and its developer platform.

---

Feel free to contribute by submitting pull requests or reporting issues.  
Contributions are welcome! 🚀

Cheers,  
Victor