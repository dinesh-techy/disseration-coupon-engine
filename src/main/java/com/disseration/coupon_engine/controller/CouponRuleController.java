package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.CouponDeleteDTO;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/rule")
@RestController
public class CouponRuleController {
    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<CouponRule> getFinalizedCouponRuleById(@RequestParam String couponId){
        CouponRule couponRule = couponService.getCouponRuleById(couponId);
        return ResponseEntity.status(200).body(couponRule);
    }

    @GetMapping("byType")
    public ResponseEntity<List<CouponRule>> getCouponByType(@RequestParam String couponType){
        List<CouponRule> couponRule = couponService.getCouponRuleByType(couponType);
        return ResponseEntity.status(200).body(couponRule);
    }

    @DeleteMapping
    public ResponseEntity<CouponDeleteDTO> deleteCouponRuleById(@RequestParam String couponId){
        CouponDeleteDTO deletedCouponRuleById = couponService.deleteCouponRuleById(couponId);
        return ResponseEntity.status(200).body(deletedCouponRuleById);
    }
}
