package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.CouponDeleteDTO;
import com.disseration.coupon_engine.dto.FinalizeRuleRequest;
import com.disseration.coupon_engine.dto.RuleDraft;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.entity.GenerateNewRule;
import com.disseration.coupon_engine.service.CouponService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/draftRule")
@RestController
public class CouponRuleDraftController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<RuleDraft> newDraftRule(@RequestBody GenerateNewRule generateNewRule) throws JsonProcessingException {
        RuleDraft couponRule = couponService.generateCouponRuleGemini(generateNewRule.getNewRule());
        return ResponseEntity.status(201).body(couponRule);
    }

    @GetMapping
    public ResponseEntity<CouponRuleDraft> getRuleDraftById(@RequestParam String id){
        CouponRuleDraft couponRuleDraft = couponService.getRuleDraftById(id);
        return ResponseEntity.status(200).body(couponRuleDraft);
    }

    @DeleteMapping
    public ResponseEntity<CouponDeleteDTO> deleteRuleDraftById(@RequestParam String id){
        CouponDeleteDTO couponDeleteDTO = couponService.deleteRuleDraftById(id);
        return ResponseEntity.status(200).body(couponDeleteDTO);
    }

    @PostMapping("/finalizeRule")
    public ResponseEntity<CouponRule> finalizeRule(@RequestBody FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        CouponRule couponRule = couponService.finalizeRule(finalizeRuleRequest);
        return ResponseEntity.status(200).body(couponRule);
    }
}