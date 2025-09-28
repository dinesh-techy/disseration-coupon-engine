package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.api.OllamaApi;
import com.disseration.coupon_engine.dto.OllamaResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    @Autowired
    private OllamaApi ollamaApi;

    @Autowired
    private ObjectMapper objectMapper;

    public String generateText(String prompt){
        return ollamaApi.generateText(prompt);
    }

    public OllamaResponse generateRule(String description) throws JsonProcessingException {
        // Strict prompt to force JSON
        String prompt = """
                You are a rule generator for a coupon engine.
                Task:
                - Convert the given description into a strict JSON object.
                - ONLY include fields explicitly provided in the description.
                - If a required field cannot be inferred, set it to null or an empty array.
                Output format:
                {
                "type": "DISCOUNT | CASHBACK | BUY_ONE_GET_ONE | null",
                "value": number or null,
                "conditions": []
                }
                Rules:
                1. "type":
                - DISCOUNT if description says discount.
                - CASHBACK if cashback.
                - BUY_ONE_GET_ONE if BOGO.
                - null if unclear.
                2. "value":
                - For DISCOUNT or CASHBACK → decimal between 0 and 1 (e.g., 0.20 for 20%).
                - For BUY_ONE_GET_ONE or missing → null.
                3. "conditions":
                - Must be a JSON array of strings.
                - Each string must come **directly from the description text**.
                - Do NOT create or assume attributes.
                - If none are explicitly mentioned, use [].
                - Always use lowercase_snake_case.
                4. If a field cannot be inferred → set it to null (or [] for conditions).
                5. Output ONLY the JSON. No text or explanation.
                Description: %s
        """ + description;

        String rawResponse = ollamaApi.generateText(prompt);

        return objectMapper.readValue(rawResponse, OllamaResponse.class);
    }

}
