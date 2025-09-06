package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class RuleParserService {

    private final ObjectMapper objectMapper;

    public RuleParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public Rule parse(String rawResponse) {
        if (rawResponse == null) {
            throw new IllegalArgumentException("Raw response is null");
        }

        String normalizedJson = normalizeJson(rawResponse);

        try {
            return objectMapper.readValue(normalizedJson, Rule.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON into Rule: " + e.getMessage(), e);
        }
    }

    /**
     * Cleans LLM response so it’s always valid JSON.
     */
    private String normalizeJson(String raw) {
        String cleaned = raw.trim();

        // Case 1: remove markdown fences like ``` or ```json
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("(?s)```(json)?", "").trim();
        }

        // Case 2: response is an escaped JSON string (starts with "{)
        if (cleaned.startsWith("\"{")) {
            try {
                cleaned = objectMapper.readValue(cleaned, String.class); // unescape
            } catch (Exception e) {
                throw new RuntimeException("Failed to unescape JSON: " + e.getMessage(), e);
            }
        }

        return cleaned;
    }
}
