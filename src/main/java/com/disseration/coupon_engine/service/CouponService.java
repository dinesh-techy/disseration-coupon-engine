package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.FinalizeRuleRequest;
import com.disseration.coupon_engine.dto.OllamaResponse;
import com.disseration.coupon_engine.dto.Rule;
import com.disseration.coupon_engine.dto.RuleDraft;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.repository.CouponRuleDraftRepository;
import com.disseration.coupon_engine.repository.CouponRuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService {

    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;
    private final CouponRuleDraftRepository couponRuleDraftRepository;
    private final CouponRuleRepository couponRuleRepository;


    public CouponService(OllamaService ollamaService, ObjectMapper objectMapper, RuleParserService ruleParserService, CouponRuleDraftRepository couponRuleDraftRepository, CouponRuleRepository ruleRepo){
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
        this.couponRuleDraftRepository = couponRuleDraftRepository;
        this.couponRuleRepository = ruleRepo;
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
            couponRuleDraftRepository.save(entity);
            return ruleDraft;
        }
        catch (JsonProcessingException jsonProcessingException){
            throw new RuntimeException("JSON Processing Exception: " + jsonProcessingException.getMessage());
        }
        catch (Exception e) {
            return new RuleDraft(null, List.of("Invalid JSON from LLM: " + e.getMessage()));
        }
    }

    public CouponRule finalizeRule(FinalizeRuleRequest finalizeRuleRequest){
        CouponRuleDraft couponRuleDraft = couponRuleDraftRepository.findById(finalizeRuleRequest.getDraftId())
                .orElseThrow(()->new EntityNotFoundException("Rule Draft not found"));
        // Merge fields
        CouponRule finalRule = new CouponRule();
        finalRule.setDescription(couponRuleDraft.getDescription());
        finalRule.setRuleJson(couponRuleDraft.getParsedJson()); // already structured JSON
        finalRule.setExpiryDate(finalizeRuleRequest.getExpiryDate());
        finalRule.setUsageLimit(finalizeRuleRequest.getUsageLimit());
        finalRule.setStatus(CouponRule.Status.FINALIZED);

        // Save final rule
        CouponRule finalizedRule = couponRuleRepository.save(finalRule);

        // Update draft status so it's no longer pending
        couponRuleDraft.setStatus(CouponRuleDraft.Status.VALIDATED);
        couponRuleDraftRepository.save(couponRuleDraft);

        return finalizedRule;
    }
}
