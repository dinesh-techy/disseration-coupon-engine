package com.disseration.coupon_engine.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaApi {

    private final RestTemplate restTemplate;

    public OllamaApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateText(String prompt) {
        String url = "http://localhost:11434/api/generate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", "llama3.1",
                "prompt", prompt,
                "stream", false   // 👈 force non-streaming
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);

        return response != null ? response : "No response from Ollama";
    }
}
