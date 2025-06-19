package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.CollectDataRequestDTO;
import com.cabybara.aishortvideo.dto.request.create_video.GenerateScriptRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.CollectDataResponseDTO;
import com.cabybara.aishortvideo.service.CollectDataService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;


@Service
@Slf4j
public class CollectDataServiceImpl implements CollectDataService {
    private final RestTemplate restTemplate;

    @Value("${data.api.wikipedia.url}")
    private String wikipediaUrl;

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
        } else if ("wikidata".equalsIgnoreCase(source)) {
            fullText = fetchWikidataSummary(query, lang);
        } else if ("ai".equalsIgnoreCase(source)) {

        }

        return CollectDataResponseDTO.builder()
                .source(request.getSource())
                .lang(request.getLang())
                .text(fullText)
                .build();
    }

    // Call API from wikipedia
    private String fetchFromWikipedia(String query, String lang) {
        try {
            String apiUrl = String.format(
                    wikipediaUrl,
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

    // Call from Wikidata
    private String fetchWikidataSummary(String query, String lang) {
        try {
            String apiUrl = String.format(
                    "https://www.wikidata.org/w/api.php?action=wbsearchentities&search=%s&language=%s&format=json",
                    URLEncoder.encode(query, StandardCharsets.UTF_8),
                    lang);

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
}
