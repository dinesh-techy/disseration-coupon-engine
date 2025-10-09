package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.api.GeminiApi;
import com.disseration.coupon_engine.dto.GeminiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GeminiApi geminiApi;

    public GeminiService(GeminiApi geminiApi) {
        this.geminiApi = geminiApi;
    }

    public GeminiResponse generateText(String prompt){
        return geminiApi.generateText(prompt);
    }

    public GeminiResponse generateRule(String description) throws JsonProcessingException {
        // Strict prompt to force JSON
        String prompt = """
                You are a rule generator for a coupon engine.
                Task:
                - Convert the given description into a strict JSON object.
                - ONLY include fields explicitly provided in the description.
                - If a required field cannot be inferred, set it to null.
                Output format:
                {
                  "type": "DISCOUNT | CASHBACK | BUY_ONE_GET_ONE | null",
                  "value": number or null,
                  "condition": {
                    "field": string or null,
                    "operator": string or null,
                    "value": string or null
                  },
                  "expiryDate": string (ISO format) or null,
                  "usageLimit": number or null,
                  "stackable": boolean or null
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
                3. "condition":
                   - Must be a single JSON object with keys: field, operator, value.
                   - Derive field/operator/value directly from the description.
                   - Use lowercase_snake_case for field names.
                   - If no condition is mentioned, set the entire object to null.
                4. "expiryDate", "usageLimit", "stackable":
                   - Include only if explicitly mentioned; otherwise null.
                5. If a field cannot be inferred → set it to null.
                6. Output ONLY the JSON. No text or explanation.
                Description: %s
        """ + description;

        GeminiResponse rawResponse = geminiApi.generateText(prompt);
        return rawResponse;

    }

}
