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

        // Case 1: Remove markdown code fences like ``` or ```json
        cleaned = cleaned.replaceAll("(?s)```(?:json)?", "").trim();

        // Case 2: Unescape if it’s a quoted JSON string (starts with "{" but inside quotes)
        if (cleaned.startsWith("\"{") && cleaned.endsWith("}\"")) {
            try {
                cleaned = objectMapper.readValue(cleaned, String.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to unescape JSON: " + e.getMessage(), e);
            }
        }

        // Case 3: Remove invisible Unicode chars (like BOM or zero-width space)
        cleaned = cleaned.replaceAll("[\\uFEFF\\u200B-\\u200F]", "");

        // Case 4: Sanity check — JSON must start with { and end with }
        if (!cleaned.startsWith("{") || !cleaned.endsWith("}")) {
            throw new RuntimeException("Normalized string is not valid JSON: " + cleaned);
        }

        return cleaned;
    }
}
