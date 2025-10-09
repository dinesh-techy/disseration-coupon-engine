package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.*;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.repository.CouponRuleDraftRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponRuleDraftService {

    private final CouponRuleDraftRepository couponRuleDraftRepository;
    private final GeminiService geminiService;
    private final CouponTransformer couponTransformer;

    public CouponRuleDraftService(CouponRuleDraftRepository couponRuleDraftRepository, GeminiService geminiService, CouponTransformer couponTransformer) {
        this.couponRuleDraftRepository = couponRuleDraftRepository;
        this.geminiService = geminiService;
        this.couponTransformer = couponTransformer;
    }

//    public RuleDraft generateCouponDraftRule(String newCouponDetails){
//        try {
//
//            OllamaResponse rawRuleResponse = ollamaService.generateRule(newCouponDetails);
//
//            RuleParserService parser = new RuleParserService(objectMapper);
//            Rule rule = parser.parse(rawRuleResponse.getResponse());
//
//            if (rawRuleResponse.getResponse() == null) {
//                throw new RuntimeException("Ollama returned null response");
//            }
//
//            // Validate required fields
//            List<String> missing = new ArrayList<>();
//            if (rule.getType() == null) missing.add("type");
//            if (rule.getValue() == null) missing.add("value");
//            if (rule.getConditions() == null || rule.getConditions().isEmpty()) missing.add("conditions");
//            RuleDraft ruleDraft = new RuleDraft(rule, missing);
//            // Build entity
//            CouponRuleDraft entity = CouponRuleDraft.builder()
//                    .description("Generated coupon rule")
//                    .rawResponse(rawRuleResponse.getResponse())
//                    .parsedJson(rule)   // store JSON
//                    .missingFields(missing) // store missing fields as JSON
//                    .status(missing.isEmpty() ? CouponRuleDraft.Status.VALIDATED : CouponRuleDraft.Status.FAILED)
//                    .build();
//
//            // Save entity
//            CouponRuleDraft savedDraft = couponRuleDraftRepository.saveAndFlush(entity);
//            ruleDraft.setRuleDraftId(savedDraft.getId());
//            return ruleDraft;
//        }
//        catch (JsonProcessingException jsonProcessingException){
//            throw new RuntimeException("JSON Processing Exception: " + jsonProcessingException.getMessage());
//        }
//        catch (Exception e) {
//            return new RuleDraft(null, List.of("Invalid JSON from LLM: " + e.getMessage()));
//        }
//    }

    public CouponRuleDraft getRuleDraftById(String ruleDraftId){
        Optional<CouponRuleDraft> ruleDraft = couponRuleDraftRepository.findById(UUID.fromString(ruleDraftId));
        return ruleDraft.orElseThrow();
    }

    public CouponDeleteDTO deleteRuleDraftById(String ruleDraftId){
        couponRuleDraftRepository.deleteById(UUID.fromString(ruleDraftId));
        return new CouponDeleteDTO(UUID.fromString(ruleDraftId),"Coupon Draft Rule deleted!");
    }

    public RuleDraft generateCouponDraftRule(String newCouponDetails){
        try {

            GeminiResponse rawRuleResponse = geminiService.generateRule(newCouponDetails);
            // Transform Gemini Response into Rule Json
            Rule rule = couponTransformer.transformGeminiRuleGenerationResponse(rawRuleResponse);

            // RuleDraft Object creation
            RuleDraft ruleDraft = ruleDraftValidation(rule);

            // Build entity to store in DB
            CouponRuleDraft entity = CouponRuleDraft.builder()
                    .description("Generated coupon rule")
                    .rawResponse(rule.toString())
                    .parsedJson(rule)   // store JSON
                    .missingFields(ruleDraft.getMissingFields()) // store missing fields as JSON
                    .ruleType(rule.getType())
                    .status(ruleDraft.getMissingFields().isEmpty() ? CouponRuleDraft.Status.VALIDATED : CouponRuleDraft.Status.FAILED)
                    .build();

            // Save entity
            CouponRuleDraft savedDraft = couponRuleDraftRepository.saveAndFlush(entity);
            ruleDraft.setRuleDraftId(savedDraft.getId());
            return ruleDraft;
        }
        catch (JsonProcessingException jsonProcessingException){
            throw new RuntimeException("JSON Processing Exception: " + jsonProcessingException.getMessage());
        }
        catch (Exception e) {
            return new RuleDraft(null, List.of("Invalid JSON from LLM: " + e.getMessage()));
        }
    }

    private RuleDraft ruleDraftValidation(Rule rule){
        // Validate required fields
        List<String> missing = new ArrayList<>();
        if (rule.getType() == null) missing.add("type");
        if (rule.getValue() == null) missing.add("value");
        if (rule.getConditions() == null || rule.getConditions().isEmpty()) missing.add("conditions");
        return new RuleDraft(rule, missing);
    }
}

