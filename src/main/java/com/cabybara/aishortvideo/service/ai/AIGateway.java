package com.cabybara.aishortvideo.service.ai;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
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

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AIGateway {
    private final RestTemplate restTemplate;

    // Account for calling model AI
    @Value("${account.cloudflare.apiToken}")
    private String apiToken;
    @Value("${account.cloudflare.accountId}")
    private String accountId;

    // API chat model
    @Value("${ai.chat.api}")
    private String aiChatApi;
    @Value("${model.chat}")
    private String modelChat;

    // API image model
    private final Executor executor = Executors.newFixedThreadPool(3);
    @Value("${ai.image.api}")
    private String aiImageApi;
    @Value("${model.image}")
    private String modelImage;
    private final int WIDTH_IMAGE = 1080;
    private final int HEIGHT_IMAGE = 1920;

    // API audio model
    @Value("${ai.audio.api}")
    private String apiAudioApi;
    @Value("${account.azure.key}")
    private String azureKey;


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

    // Call image model
    public CompletableFuture<String> callImageModelAIAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> callImageModelAI(prompt), executor);
    }

    public List<String> generateThreeImagesAsync(String prompt) {
        CompletableFuture<String> image1 = callImageModelAIAsync(prompt);
        CompletableFuture<String> image2 = callImageModelAIAsync(prompt);
        CompletableFuture<String> image3 = callImageModelAIAsync(prompt);

        // Chờ cả 3 hoàn thành
        CompletableFuture<Void> all = CompletableFuture.allOf(image1, image2, image3);

        // Khi xong thì lấy kết quả
        all.join(); // đợi tất cả

        return Arrays.asList(image1.join(), image2.join(), image3.join());
    }

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

    // Call audio model
    public String callAudioModelAI(GenerateAudioRequestDTO request) {
        String url = apiAudioApi;

        HttpHeaders headers = new HttpHeaders();
        MediaType ssmlType = new MediaType("application", "ssml+xml", StandardCharsets.UTF_8);
        headers.setContentType(ssmlType);
        headers.set("Ocp-Apim-Subscription-Key", azureKey);
        headers.set("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3");

        String lang = "";
        if ("vi".equals(request.getLang().getValue())) {
            lang = "vi-VN";
        } else if ("en".equals(request.getLang().getValue())) {
            lang = "en-US";
        } else if ("zh".equals(request.getLang().getValue())) {
            lang = "zh-CN";
        }
        System.out.println("LANG SPEAK: " + lang);

        String ssml = String.format(
                "<speak version='1.0' xml:lang='%s'>" +
                        "<voice name='%s'>" +
                        "<prosody rate='%s'>" +
                        "%s" +
                        "</prosody>" +
                        "</voice>" +
                        "</speak>",
                lang,
                request.getVoiceType(),
                request.getSpeed() + "%", // Azure sử dụng % cho speaking rate (0.5 = 50%, 1.0 = 100%, 1.5 = 150%)
                request.getScript()
        );

        System.out.println("SSML: " + ssml);

        HttpEntity<String> audioRequest = new HttpEntity<>(ssml, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    audioRequest,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Trả về audio dưới dạng base64
                return Base64.getEncoder().encodeToString(response.getBody());
            } else {
                throw new RuntimeException("Azure TTS API call failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while calling Azure TTS API: " + e.getMessage(), e);
        }
    }
}
