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
        
        Output format (and rules):
        {
          "type": "<DISCOUNT | CASHBACK | BUY_ONE_GET_ONE | null>",
          "value": <decimal between 0 and 1 for DISCOUNT or CASHBACK, null for BUY_ONE_GET_ONE or if not specified>,
          "conditions": [ "<condition1>", "<condition2>", ... ]  // empty array if none given
        }
        
        Strict Rules:
        1. "type" must be one of DISCOUNT, CASHBACK, BUY_ONE_GET_ONE, or null if not clear.
        2. "value":
           - For DISCOUNT or CASHBACK → decimal between 0 and 1 (e.g., 0.10 = 10%).
           - For BUY_ONE_GET_ONE or missing → null.
        3. "conditions":
           - Always an array.
           - Only include conditions explicitly mentioned in description.
           - Use lowercase snake_case format (e.g., "is_email_subscribed").
        4. Do NOT add assumptions, defaults, or extra fields.
        5. Do NOT return markdown, explanations, or extra text. Return ONLY the JSON.
        
        Description: %s
        """ + description;




        String rawResponse = ollamaApi.generateText(prompt);

        return objectMapper.readValue(rawResponse, OllamaResponse.class);
    }

}
