package com.disseration.coupon_engine.api;

import com.disseration.coupon_engine.dto.GeminiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiApi {

    private final RestTemplate restTemplate;

    public GeminiApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

        public GeminiResponse generateText(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-goog-api-key","AIzaSyDQjsMZtHg2qcVWuMPY-z5R6WJajnOzdW4");

        Map<String, Object> contentPart = Map.of(
                "text", prompt
        );

        Map<String, Object> content = Map.of(
                "role", "user",
                "parts", List.of(contentPart)
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(content)
        );


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, entity, GeminiResponse.class);
    }

}
