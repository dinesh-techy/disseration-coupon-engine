package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.FinalizeRuleRequest;
import com.disseration.coupon_engine.dto.GeminiResponse;
import com.disseration.coupon_engine.dto.Rule;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    public CouponRule createFinalCouponRuleObject(CouponRuleDraft couponRuleDraft, FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        // Merge fields
        CouponRule finalRule = new CouponRule();
        finalRule.setId(couponRuleDraft.getId());
        finalRule.setDescription(couponRuleDraft.getDescription());
        finalRule.setRuleType(couponRuleDraft.getRuleType());

        // ✅ Properly configured ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Parse the existing JSON string
        JsonNode jsonNode = objectMapper.valueToTree(couponRuleDraft.getParsedJson());

        // Cast to ObjectNode to allow modification
        ObjectNode objectNode = (ObjectNode) jsonNode;

        objectNode.put("expiryDate", String.valueOf(finalizeRuleRequest.getExpiryDate()));
        objectNode.put("usageLimit", finalizeRuleRequest.getUsageLimit());
        objectNode.put("stackable", true);

        String updatedJson = objectMapper.writeValueAsString(objectNode);

        // ✅ Deserialize JSON string into Rule object (LocalDate handled)
        Rule updatedRuleJson = objectMapper.readValue(updatedJson, Rule.class);

        // Set to finalRule
        finalRule.setRuleJson(updatedRuleJson);
        finalRule.setExpiryDate(finalizeRuleRequest.getExpiryDate());
        finalRule.setUsageLimit(finalizeRuleRequest.getUsageLimit());
        finalRule.setStatus(CouponRule.Status.FINALIZED);

        return  finalRule;
    }
}
