package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.*;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponRuleService {

    private final CouponRuleDraftRepository couponRuleDraftRepository;
    private final CouponRuleRepository couponRuleRepository;
    private final CouponTransformer couponTransformer;

    public CouponRuleService(OllamaService ollamaService, ObjectMapper objectMapper, RuleParserService ruleParserService, CouponRuleDraftRepository couponRuleDraftRepository, CouponRuleRepository ruleRepo, GeminiService geminiService, CouponTransformer couponTransformer){
        this.couponRuleDraftRepository = couponRuleDraftRepository;
        this.couponRuleRepository = ruleRepo;
        this.couponTransformer = couponTransformer;
    }

    public CouponRule getCouponRuleById(String couponRuleId){
        Optional<CouponRule> couponRule = couponRuleRepository.findById(UUID.fromString(couponRuleId));
        return  couponRule.orElseThrow();
    }

    public List<CouponRule> getCouponRuleByType(String couponType){
        return couponRuleRepository.findByRuleType(couponType);
    }

    public CouponDeleteDTO deleteCouponRuleById(String couponRuleId){
        couponRuleRepository.deleteById(UUID.fromString(couponRuleId));
        return new CouponDeleteDTO(UUID.fromString(couponRuleId),"Coupon Rule deleted!");
    }

    public CouponRule finalizeRule(FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        CouponRuleDraft couponRuleDraft = couponRuleDraftRepository.findById(finalizeRuleRequest.getDraftId())
                .orElseThrow(()->new EntityNotFoundException("Rule Draft not found"));

        // Final CouponRule Object creation
        CouponRule finalRule = couponTransformer.createFinalCouponRuleObject(couponRuleDraft,finalizeRuleRequest);

        // Save final rule
        CouponRule finalizedRule = couponRuleRepository.saveAndFlush(finalRule);

        // Update draft status so it's no longer pending
        couponRuleDraft.setStatus(CouponRuleDraft.Status.VALIDATED);
        couponRuleDraftRepository.save(couponRuleDraft);

        return finalizedRule;
    }
}
