package com.cabybara.aishortvideo.service.create_video.implement;

import com.cabybara.aishortvideo.dto.request.create_video.CollectDataRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.CollectDataResponseDTO;
import com.cabybara.aishortvideo.service.create_video.CollectDataService;

import com.cabybara.aishortvideo.service.ai.AIGateway;
import com.cabybara.aishortvideo.utils.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final int MAX_LENGTH = 30;

    @Override
    public CollectDataResponseDTO collectData(CollectDataRequestDTO request) {
        String source = request.getSource().getValue();
        String query = request.getQuery();
        Language lang = request.getLang();

        String fullText = "";

        if ("wikipedia".equalsIgnoreCase(source)) {
            fullText = fetchFromWikipedia(query, lang.getValue());
        } else if ("wikidata".equalsIgnoreCase(source)) {
            fullText = fetchWikidataSummary(query, lang.getValue());
        } else if ("ai".equalsIgnoreCase(source)) {
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
                "Respond in '%s' with concise and accurate information about the topic: '%s'.\n" +
                        "- Maximum of %d words.\n" +
                        "- Provide only factual information. Do not introduce the topic, and do not ask follow-up questions.\n" +
                        "- If no relevant information is found, return exactly: 'No relevant data found'.\n" +
                        "- Prioritize trustworthy sources (books, academic research, official websites).\n" +
                        "- IMPORTANT: Return only the content. Do not include greetings, explanations, or search process.\n",
                lang, query, MAX_LENGTH
        );
    }
}
