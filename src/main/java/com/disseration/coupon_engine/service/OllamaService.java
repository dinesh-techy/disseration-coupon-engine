package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    @Autowired
    private OllamaApi ollamaApi;

    public String generateText(String prompt){
        return ollamaApi.generateText(prompt);
    }

}
