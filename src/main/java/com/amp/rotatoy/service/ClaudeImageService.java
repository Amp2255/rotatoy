package com.amp.rotatoy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ClaudeImageService {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeImageService.class);

    @Value("${claude.api.key}")
    private String apiKey;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String CLAUDE_MODEL = "claude-haiku-4-5-20251001";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, List<String>> getImageSuggestions(String base64ImageData) {
        String base64 = base64ImageData.startsWith("data:image/jpeg;base64,")
                ? base64ImageData.substring("data:image/jpeg;base64,".length())
                : base64ImageData;

        Map<String, Object> imageSource = new LinkedHashMap<>();
        imageSource.put("type", "base64");
        imageSource.put("media_type", "image/jpeg");
        imageSource.put("data", base64);

        Map<String, Object> imageContent = new LinkedHashMap<>();
        imageContent.put("type", "image");
        imageContent.put("source", imageSource);

        Map<String, Object> textContent = new LinkedHashMap<>();
        textContent.put("type", "text");
        textContent.put("text",
                "Analyze this toy image and return ONLY a JSON object with exactly two fields: " +
                "\"nameSuggestions\" (array of 5 short name ideas for this toy, e.g. brand, type, colour+type) " +
                "and \"notesSuggestions\" (array of 5 short descriptive notes, e.g. age range, skills developed, key features). " +
                "No markdown, no extra text — just the raw JSON object.");

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");
        message.put("content", Arrays.asList(imageContent, textContent));

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", CLAUDE_MODEL);
        requestBody.put("max_tokens", 500);
        requestBody.put("messages", Collections.singletonList(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(CLAUDE_API_URL, HttpMethod.POST, entity, Map.class);
            return parseClaudeResponse(response.getBody());
        } catch (Exception e) {
            logger.error("Claude API call failed: {}", e.getMessage());
            throw new RuntimeException("Failed to analyze image: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> parseClaudeResponse(Map<?, ?> responseBody) {
        try {
            List<?> content = (List<?>) responseBody.get("content");
            Map<?, ?> first = (Map<?, ?>) content.get(0);
            String text = ((String) first.get("text")).trim();
            return objectMapper.readValue(text, new TypeReference<Map<String, List<String>>>() {});
        } catch (Exception e) {
            logger.error("Failed to parse Claude response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse image analysis response");
        }
    }
}
