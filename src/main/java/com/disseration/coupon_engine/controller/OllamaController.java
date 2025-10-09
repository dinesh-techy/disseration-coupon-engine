package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.GeminiResponse;
import com.disseration.coupon_engine.service.GeminiService;
import com.disseration.coupon_engine.service.OllamaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ollama")
public class OllamaController {

    private final OllamaService ollamaService;
    private final GeminiService geminiService;

    public OllamaController(OllamaService ollamaService, GeminiService geminiService) {
        this.ollamaService = ollamaService;
        this.geminiService = geminiService;
    }

    @GetMapping("/generate")
    public String generate(@RequestParam String prompt) {
        return ollamaService.generateText(prompt);
    }

    @GetMapping("/gemini/generate")
    public GeminiResponse generateGemini(@RequestParam String prompt) {
        return geminiService.generateText(prompt);
    }
}
