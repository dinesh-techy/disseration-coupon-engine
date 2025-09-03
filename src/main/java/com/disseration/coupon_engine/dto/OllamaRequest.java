package com.disseration.coupon_engine.dto;

import lombok.Data;

@Data
public class OllamaRequest {
    private String model;
    private String prompt;

    public OllamaRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
    }

    // getters and setters
}

