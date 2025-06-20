package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.CollectDataRequestDTO;
import com.cabybara.aishortvideo.dto.request.create_video.GenerateScriptRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.CollectDataResponseDTO;
import com.cabybara.aishortvideo.service.CollectDataService;

import com.cabybara.aishortvideo.service.ai.AIGateway;
import com.cabybara.aishortvideo.utils.DataSource;
import com.cabybara.aishortvideo.utils.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class CollectDataServiceImpl implements CollectDataService {
    private final RestTemplate restTemplate;

    private final AIGateway aiGateway;

    @Value("${data.api.wikipedia.url}")
    private String wikipediaUrl;

    @Value("${data.api.wikidata.url}")
    private String wikidataUrl;

    private final int MAX_LENGTH = 50;

    @Override
    public CollectDataResponseDTO collectData(CollectDataRequestDTO request) {
        DataSource source = request.getSource();
//        System.out.println("DATA SOURCE: " + source);
        String query = request.getQuery();
        Language lang = request.getLang();

        String fullText = "";

        if ("wikipedia".equalsIgnoreCase(source.getValue())) {
            fullText = fetchFromWikipedia(query, lang.getValue());
        } else if ("wikidata".equalsIgnoreCase(source.getValue())) {
            fullText = fetchWikidataSummary(query, lang.getValue());
        } else if ("ai".equalsIgnoreCase(source.getValue())) {
            String prompt = collectDataPrompt(query, lang.name());
            fullText = aiGateway.callChatModelAI(prompt);
        }

        return CollectDataResponseDTO.builder()
                .source(request.getSource().name())
                .lang(request.getLang().name())
                .text(fullText)
                .build();
    }

    // Call API from Wikipedia
    private String fetchFromWikipedia(String query, String lang) {
        try {
            String apiUrl = String.format(
                    wikipediaUrl, lang, query);

            // Call API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Failed to fetch data from Wikipedia API");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode pagesNode = rootNode.path("query").path("pages");

            Iterator<Map.Entry<String, JsonNode>> fields = pagesNode.fields();
            if (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                return entry.getValue().path("extract").asText();
            }

            return "Can't find content from your topic on Wikipedia";
        } catch (Exception e) {
            throw new RuntimeException("Error while getting data from Wikipedia: " + e.getMessage(), e);
        }
    }

    // Call API from Wikidata
    private String fetchWikidataSummary(String query, String lang) {
        try {
            String apiUrl = String.format(
                    wikidataUrl, query, lang, lang);

            // Call API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Failed to fetch data from Wikidata API");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode searchResults = rootNode.path("search");

            if (searchResults.isEmpty()) {
                return "No Wikidata entry found for: " + query;
            }

            JsonNode firstResult = searchResults.get(0);
            String label = firstResult.path("label").asText("(No label available)");
            String description = firstResult.path("description").asText("(No description available)");

            return String.format("%s: %s", label, description);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Wikidata summary: " + e.getMessage(), e);
        }
    }

    private String collectDataPrompt(String query, String lang) {
        return String.format(
                "Hãy tìm kiếm thông tin về '%s' và trả lời bằng ngôn ngữ '%s'. " +
                        "Yêu cầu:\n" +
                        "1. Câu trả lời phải chính xác, ngắn gọn, tập trung vào chủ đề.\n" +
                        "2. Giới hạn tối đa %d từ.\n" +
                        "3. Nếu không tìm thấy kết quả, hãy thông báo 'Không có dữ liệu phù hợp'.\n" +
                        "4. Ưu tiên nguồn tin cậy (sách, nghiên cứu, trang web chính thức)." +
                        "QUAN TRỌNG: Chỉ trả về nội dung câu trả lời không định dạng",
                query, lang, MAX_LENGTH
        );
    }
}
