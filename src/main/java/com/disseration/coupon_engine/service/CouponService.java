package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.*;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.repository.CouponRuleDraftRepository;
import com.disseration.coupon_engine.repository.CouponRuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponService {

    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;
    private final CouponRuleDraftRepository couponRuleDraftRepository;
    private final CouponRuleRepository couponRuleRepository;
    private final GeminiService geminiService;


    public CouponService(OllamaService ollamaService, ObjectMapper objectMapper, RuleParserService ruleParserService, CouponRuleDraftRepository couponRuleDraftRepository, CouponRuleRepository ruleRepo, GeminiService geminiService){
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
        this.couponRuleDraftRepository = couponRuleDraftRepository;
        this.couponRuleRepository = ruleRepo;
        this.geminiService = geminiService;
    }

    public RuleDraft generateCouponRule(String newCouponDetails){
        try {

            OllamaResponse rawRuleResponse = ollamaService.generateRule(newCouponDetails);

            RuleParserService parser = new RuleParserService(objectMapper);
            Rule rule = parser.parse(rawRuleResponse.getResponse());

            if (rawRuleResponse.getResponse() == null) {
                throw new RuntimeException("Ollama returned null response");
            }

            // Validate required fields
            List<String> missing = new ArrayList<>();
            if (rule.getType() == null) missing.add("type");
            if (rule.getValue() == null) missing.add("value");
            if (rule.getConditions() == null || rule.getConditions().isEmpty()) missing.add("conditions");
            RuleDraft ruleDraft = new RuleDraft(rule, missing);
            // Build entity
            CouponRuleDraft entity = CouponRuleDraft.builder()
                    .description("Generated coupon rule")
                    .rawResponse(rawRuleResponse.getResponse())
                    .parsedJson(objectMapper.writeValueAsString(rule))   // store JSON
                    .missingFields(objectMapper.writeValueAsString(missing)) // store missing fields as JSON
                    .status(missing.isEmpty() ? CouponRuleDraft.Status.VALIDATED : CouponRuleDraft.Status.FAILED)
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

    public CouponRule finalizeRule(FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        CouponRuleDraft couponRuleDraft = couponRuleDraftRepository.findById(finalizeRuleRequest.getDraftId())
                .orElseThrow(()->new EntityNotFoundException("Rule Draft not found"));
        // Merge fields
        CouponRule finalRule = new CouponRule();
        finalRule.setDescription(couponRuleDraft.getDescription());
        finalRule.setRuleType(couponRuleDraft.getRuleType());

        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the existing JSON string
        JsonNode jsonNode = objectMapper.readTree(couponRuleDraft.getParsedJson());

        // Cast to ObjectNode to allow modification
        ObjectNode objectNode = (ObjectNode) jsonNode;

        objectNode.put("expiryDate", "2025-12-31");
        objectNode.put("usageLimit", 5);
        objectNode.put("stackable", true);

        String updatedJson = objectMapper.writeValueAsString(objectNode);

        // Set to finalRule
        finalRule.setRuleJson(updatedJson);
        finalRule.setExpiryDate(finalizeRuleRequest.getExpiryDate());
        finalRule.setUsageLimit(finalizeRuleRequest.getUsageLimit());
        finalRule.setStatus(CouponRule.Status.FINALIZED);

        // Save final rule
        CouponRule finalizedRule = couponRuleRepository.saveAndFlush(finalRule);

        // Update draft status so it's no longer pending
        couponRuleDraft.setStatus(CouponRuleDraft.Status.VALIDATED);
        couponRuleDraftRepository.save(couponRuleDraft);

        return finalizedRule;
    }

    public CouponRule getCouponRuleById(String couponRuleId){
        Optional<CouponRule> couponRule = couponRuleRepository.findById(UUID.fromString(couponRuleId));
        return  couponRule.orElseThrow();
    }

    public CouponDeleteDTO deleteCouponRuleById(String couponRuleId){
        couponRuleRepository.deleteById(UUID.fromString(couponRuleId));
        return new CouponDeleteDTO(UUID.fromString(couponRuleId),"Coupon Rule deleted!");
    }

    // Gemini API logic
    public RuleDraft generateCouponRuleGemini(String newCouponDetails){
        try {

            GeminiResponse rawRuleResponse = geminiService.generateRule(newCouponDetails);

            RuleParserService parser = new RuleParserService(objectMapper);
            Rule rule = parser.parse(rawRuleResponse.getCandidates().get(0).getContent().getParts().get(0).getText());

            if (rawRuleResponse.getCandidates() == null) {
                throw new RuntimeException("Gemini API returned null response");
            }

            // Validate required fields
            List<String> missing = new ArrayList<>();
            if (rule.getType() == null) missing.add("type");
            if (rule.getValue() == null) missing.add("value");
            if (rule.getConditions() == null || rule.getConditions().isEmpty()) missing.add("conditions");
            RuleDraft ruleDraft = new RuleDraft(rule, missing);
            // Build entity
            CouponRuleDraft entity = CouponRuleDraft.builder()
                    .description("Generated coupon rule")
                    .rawResponse(rule.toString())
                    .parsedJson(objectMapper.writeValueAsString(rule))   // store JSON
                    .missingFields(objectMapper.writeValueAsString(missing)) // store missing fields as JSON
                    .ruleType(rule.getType())
                    .status(missing.isEmpty() ? CouponRuleDraft.Status.VALIDATED : CouponRuleDraft.Status.FAILED)
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



}
