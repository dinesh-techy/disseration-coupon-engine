package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.CouponDeleteDTO;
import com.disseration.coupon_engine.dto.FinalizeRuleRequest;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.service.CouponRuleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/rule")
@RestController
@Tag(name = "Finalize Coupon Rule", description = "APIs for Coupon Rule")
public class CouponRuleController {
    @Autowired
    private CouponRuleService couponRuleService;

    @GetMapping(params = {"ruleId"})
    public ResponseEntity<CouponRule> getFinalizedCouponRuleById(@RequestParam String ruleId){
        CouponRule couponRule = couponRuleService.getCouponRuleById(ruleId);
        return ResponseEntity.status(200).body(couponRule);
    }

    @GetMapping(params = {"type"})
    public ResponseEntity<List<CouponRule>> getCouponByType(@RequestParam String type){
        List<CouponRule> couponRule = couponRuleService.getCouponRuleByType(type);
        return ResponseEntity.status(200).body(couponRule);
    }

    @PostMapping("/finalize")
    public ResponseEntity<CouponRule> finalizeRule(@RequestBody FinalizeRuleRequest finalizeRuleRequest) throws JsonProcessingException {
        CouponRule couponRule = couponRuleService.finalizeRule(finalizeRuleRequest);
        return ResponseEntity.status(200).body(couponRule);
    }

    @DeleteMapping
    public ResponseEntity<CouponDeleteDTO> deleteCouponRuleById(@RequestParam String couponId){
        CouponDeleteDTO deletedCouponRuleById = couponRuleService.deleteCouponRuleById(couponId);
        return ResponseEntity.status(200).body(deletedCouponRuleById);
    }

    @GetMapping(params = {"couponCode"})
    public ResponseEntity<CouponRule> getFinalizedCouponRuleByName(@RequestParam String couponCode){
        CouponRule couponRule = couponRuleService.getCouponRuleByCode(couponCode);
        return ResponseEntity.status(200).body(couponRule);
    }
}
