package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.GeminiResponse;
import com.disseration.coupon_engine.dto.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class CouponTransformer {

    private final ObjectMapper objectMapper;

    public CouponTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Rule transformGeminiRuleGenerationResponse(GeminiResponse geminiResponse){
        if (geminiResponse.getCandidates() == null) {
            throw new RuntimeException("Gemini API returned null response");
        }
        RuleParserService parser = new RuleParserService(objectMapper);
        Rule ruleDraft = parser.parse(geminiResponse.getCandidates().get(0).getContent().getParts().get(0).getText());
        return ruleDraft;
    }
}
