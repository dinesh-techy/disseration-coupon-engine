package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.CouponDeleteDTO;
import com.disseration.coupon_engine.dto.FinalizeRuleRequest;
import com.disseration.coupon_engine.dto.RuleDraft;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.entity.GenerateNewRule;
import com.disseration.coupon_engine.service.CouponService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rule")
@RestController
public class CouponRuleController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/generateRule")
    public ResponseEntity<RuleDraft> generateRule(@RequestBody GenerateNewRule generateNewRule) throws JsonProcessingException {
        RuleDraft couponRule = couponService.generateCouponRuleGemini(generateNewRule.getNewRule());
        return ResponseEntity.status(201).body(couponRule);
    }

    @GetMapping
    public ResponseEntity<CouponRule> getCouponRuleById(@RequestParam String couponId){
        CouponRule couponRule = couponService.getCouponRuleById(couponId);
        return ResponseEntity.status(200).body(couponRule);
    }

    @DeleteMapping
    public ResponseEntity<CouponDeleteDTO> deleteCouponRuleById(@RequestParam String couponId){
        CouponDeleteDTO deletedCouponRuleById = couponService.deleteCouponRuleById(couponId);
        return ResponseEntity.status(200).body(deletedCouponRuleById);
    }

    @PostMapping("/finalizeRule")
    public ResponseEntity<CouponRule> finalizeRule(@RequestBody FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        CouponRule couponRule = couponService.finalizeRule(finalizeRuleRequest);
        return ResponseEntity.status(200).body(couponRule);
    }
}