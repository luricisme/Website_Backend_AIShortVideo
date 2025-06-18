package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.CollectDataRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.CollectDataResponseDTO;
import com.cabybara.aishortvideo.service.CollectDataService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class CollectDataServiceImpl implements CollectDataService {
    private final RestTemplate restTemplate;

    @Autowired
    public CollectDataServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public CollectDataResponseDTO collectData(CollectDataRequestDTO request) {
        String source = request.getSource();
        String query = request.getQuery();
        String lang = request.getLang();

        String fullText = "";

        if ("wikipedia".equalsIgnoreCase(source)) {
            fullText = fetchFromWikipedia(query, lang);
        } else if ("pubmed".equalsIgnoreCase(source)) {

        }

        return CollectDataResponseDTO.builder()
                .source(request.getSource())
                .lang(request.getLang())
                .text(fullText)
                .build();
    }

    private String fetchFromWikipedia(String query, String lang) {
        try {
            String apiUrl = String.format(
                    "https://%s.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&titles=%s&format=json",
                    lang, URLEncoder.encode(query, StandardCharsets.UTF_8));
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

    private String fetchFromPubmed(String query, String lang) {
        return null;
    }
}
