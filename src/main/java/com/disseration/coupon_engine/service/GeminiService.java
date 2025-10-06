package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.api.GeminiApi;
import com.disseration.coupon_engine.dto.GeminiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
