package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateScriptRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateScriptResponseDTO;
import com.cabybara.aishortvideo.service.GenerateScriptService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GenerateScriptServiceImpl implements GenerateScriptService {
    private final RestTemplate restTemplate;

    @Value("${account.clouflare.apiToken}")
    private String apiToken;
    @Value("${account.clouflare.accountId}")
    private String accountId;


    @Value("${ai.chat.api}")
    private String aiChatApi;
    @Value("${model.chat}")
    private String modelChat;

    @Autowired
    public GenerateScriptServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GenerateScriptResponseDTO generateScript(GenerateScriptRequestDTO request) {
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
        body.put("prompt", buildFullPrompt(request));
        body.put("max_tokens", estimateTokens(request.getMaxLength()));

        // Create HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String script = extractGeneratedText(response.getBody());
                return GenerateScriptResponseDTO.builder()
                        .modelUsed("llama-2-7b-chat-int8")
                        .script(script)
                        .build();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while getting data from Wikipedia: " + e.getMessage(), e);
        }
    }

    private String buildFullPrompt(GenerateScriptRequestDTO request) {
        return String.format(
                "Hãy viết một câu chuyện liền mạch về '%s' với các yêu cầu:\n" +
                        "- Phong cách: %s\n" +
                        "- Đối tượng: %s\n" +
                        "- Ngôn ngữ: %s\n" +
                        "- Định dạng:\n" +
                        "  + Văn xuôi thuần túy (không phân cảnh, không tiêu đề)\n" +
                        "  + Ngôi kể thứ ba\n" +
                        "QUAN TRỌNG: Chỉ trả về nội dung câu chuyện không định dạng",
                request.getData(),
                request.getStyle(),
                request.getAudience(),
                request.getLang()
        );
    }

    private int estimateTokens(int wordCount) {
        return (int) (wordCount * 1.33); // Ước lượng tokens
    }

    public String extractGeneratedText(String apiResponse) throws Exception {
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
            return scriptContent
                    .replace("\n", " ")          // Thay newline bằng space
                    .replaceAll("\\s{2,}", " ")  // Thay nhiều space liên tiếp bằng 1 space
                    .trim();
        } catch (Exception e) {
            throw new Exception("Error processing API response: " + e.getMessage(), e);
        }
    }
}
