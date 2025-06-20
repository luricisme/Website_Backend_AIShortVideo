package com.cabybara.aishortvideo.service.ai;

import com.cabybara.aishortvideo.dto.response.create_video.GenerateScriptResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AIGateway {
    private final RestTemplate restTemplate;

    @Value("${account.cloudflare.apiToken}")
    private String apiToken;
    @Value("${account.cloudflare.accountId}")
    private String accountId;

    @Value("${ai.chat.api}")
    private String aiChatApi;
    @Value("${model.chat}")
    private String modelChat;

    @Value("${ai.image.api}")
    private String aiImageApi;
    @Value("${model.image}")
    private String modelImage;
    private final int WIDTH_IMAGE = 1080;
    private final int HEIGHT_IMAGE = 1920;

    @Autowired
    public AIGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Call chat model
    public String callChatModelAI(String prompt) {
        String url = UriComponentsBuilder
                .fromHttpUrl(aiChatApi)
                .path(modelChat)
                .buildAndExpand(accountId)
                .toUriString();

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);

        // Create request body
        Map<String, Object> body = new HashMap<>();
        body.put("prompt", prompt);

        // Create HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return extractGeneratedText(response.getBody());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while call API from model chat AI: " + e.getMessage(), e);
        }
    }

    // Call image model
    public String callImageModelAI(String prompt) {
        String url = UriComponentsBuilder
                .fromHttpUrl(aiImageApi)
                .path(modelImage)
                .buildAndExpand(accountId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);

        Map<String, Object> body = new HashMap<>();
        body.put("prompt", prompt);
        body.put("width", WIDTH_IMAGE);
        body.put("height", HEIGHT_IMAGE);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                byte[] imageBytes = response.getBody();
                return Base64.getEncoder().encodeToString(imageBytes);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while call API from model image AI: " + e.getMessage(), e);
        }
    }

    private String extractGeneratedText(String apiResponse) throws Exception {
        try {
            // 1. Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(apiResponse);

            // 2. Kiểm tra success flag
            if (!rootNode.path("success").asBoolean()) {
                throw new Exception("API request failed: " + rootNode.path("errors").toString());
            }

            // 3. Lấy nội dung kịch bản
            String scriptContent = rootNode.path("result")
                    .path("response")
                    .asText();

            // 4. Xử lý văn bản - QUAN TRỌNG
            return cleanTextContent(scriptContent);
        } catch (Exception e) {
            throw new Exception("Error processing API response: " + e.getMessage(), e);
        }
    }

    private String cleanTextContent(String text) {
        return text.replace("\\n", " ").replace("\n", " ");
    }
}
