package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.*;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.errorHandling.CouponDeleteException;
import com.disseration.coupon_engine.errorHandling.CouponFinalizeException;
import com.disseration.coupon_engine.errorHandling.CouponNotFoundException;
import com.disseration.coupon_engine.repository.CouponRuleDraftRepository;
import com.disseration.coupon_engine.repository.CouponRuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponRuleService {

    private final CouponRuleDraftRepository couponRuleDraftRepository;
    private final CouponRuleRepository couponRuleRepository;
    private final CouponTransformer couponTransformer;
    private final GeminiService geminiService;

    public CouponRuleService(OllamaService ollamaService, ObjectMapper objectMapper, RuleParserService ruleParserService, CouponRuleDraftRepository couponRuleDraftRepository, CouponRuleRepository ruleRepo, GeminiService geminiService, CouponTransformer couponTransformer, GeminiService geminiService1, GeminiService geminiService2){
        this.couponRuleDraftRepository = couponRuleDraftRepository;
        this.couponRuleRepository = ruleRepo;
        this.couponTransformer = couponTransformer;
        this.geminiService = geminiService;
    }

    public CouponRule getCouponRuleById(String couponRuleId){
        Optional<CouponRule> couponRule = couponRuleRepository.findById(UUID.fromString(couponRuleId));
        return couponRule.orElseThrow(() -> new CouponNotFoundException("Rule not found ruleId" + couponRuleId));
    }

    public List<CouponRule> getCouponRuleByType(String couponType){
        try{
            return couponRuleRepository.findByRuleType(couponType);
        } catch (Exception e) {
            throw new CouponNotFoundException("Specified coupon type "+couponType+" not found");
        }
    }

    public CouponDeleteDTO deleteCouponRuleById(String couponRuleId){
        try{
            couponRuleRepository.deleteById(UUID.fromString(couponRuleId));
            return new CouponDeleteDTO(UUID.fromString(couponRuleId),"Coupon Rule deleted!");
        } catch (Exception e) {
            throw new CouponDeleteException("Coupon Id "+couponRuleId + " delete failed!");
        }

    }

    public CouponRule finalizeRule(FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        CouponRuleDraft couponRuleDraft = couponRuleDraftRepository.findById(finalizeRuleRequest.getDraftId())
                .orElseThrow(()->new CouponNotFoundException("Coupon   Rule Draft not found"));
        // Final CouponRule Object creation
        CouponRule finalRule = couponTransformer.createFinalCouponRuleObject(couponRuleDraft,finalizeRuleRequest);

        // Save final rule
        CouponRule finalizedRule = couponRuleRepository.saveAndFlush(finalRule);

        // Update draft status so it's no longer pending
        couponRuleDraft.setStatus(CouponRuleDraft.Status.VALIDATED);
        try {
            couponRuleDraftRepository.save(couponRuleDraft);
            return finalizedRule;
        } catch (Exception e) {
            throw new CouponFinalizeException("CouponFinalize failed for the draftId "+finalizedRule.getId());
        }

    }
}
