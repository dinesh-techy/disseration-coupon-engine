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
        Your task:
        - Take the following description of a coupon rule and convert it into a strict JSON object.
        Output format:
        {
          "type": "<DISCOUNT | CASHBACK | BUY_ONE_GET_ONE>",
          "value": <decimal between 0 and 1 for DISCOUNT or CASHBACK, null for BUY_ONE_GET_ONE>,
          "conditions": [ "<condition1>", "<condition2>", ... ]
        }
        Rules:
        1. "type" must be exactly one of: DISCOUNT, CASHBACK, BUY_ONE_GET_ONE.
        2. "value":
           - For DISCOUNT or CASHBACK → must be a decimal between 0 and 1 (e.g., 0.10 = 10%).
           - For BUY_ONE_GET_ONE → must be null.
        3. "conditions":
           - Must always be an array (even if empty).
           - Each condition is a lowercase string using snake_case (e.g., "is_email_subscribed").
        4. Do NOT return markdown fences, explanations, or any extra text. Output only the JSON.
        
        Description: %s
        """+description;



        String rawResponse = ollamaApi.generateText(prompt);

        return objectMapper.readValue(rawResponse, OllamaResponse.class);
    }

}
