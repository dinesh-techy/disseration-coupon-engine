package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.Rule;
import com.disseration.coupon_engine.dto.RuleDraft;
import com.disseration.coupon_engine.entity.GenerateNewRule;
import com.disseration.coupon_engine.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rule")
@RestController
public class CouponRuleController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/generateRule")
    public ResponseEntity<RuleDraft> generateRule(@RequestBody GenerateNewRule generateNewRule){
        RuleDraft couponRule = couponService.generateCouponRule(generateNewRule.getNewRule());
        return ResponseEntity.status(201).body(couponRule);
    }
}