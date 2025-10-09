package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.CouponDeleteDTO;
import com.disseration.coupon_engine.dto.RuleDraft;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.entity.GenerateNewRule;
import com.disseration.coupon_engine.service.CouponRuleDraftService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/draftRule")
@RestController
public class CouponRuleDraftController {

    private final CouponRuleDraftService couponRuleDraftService;

    public CouponRuleDraftController(CouponRuleDraftService couponRuleDraftService) {
        this.couponRuleDraftService = couponRuleDraftService;
    }

    @PostMapping
    public ResponseEntity<RuleDraft> generateDraftRule(@RequestBody GenerateNewRule generateNewRule) throws JsonProcessingException {
        RuleDraft couponRule = couponRuleDraftService.generateCouponDraftRule(generateNewRule.getNewRule());
        return ResponseEntity.status(201).body(couponRule);
    }

    @GetMapping
    public ResponseEntity<CouponRuleDraft> getRuleDraftById(@RequestParam String id){
        CouponRuleDraft couponRuleDraft = couponRuleDraftService.getRuleDraftById(id);
        return ResponseEntity.status(200).body(couponRuleDraft);
    }

    @DeleteMapping
    public ResponseEntity<CouponDeleteDTO> deleteRuleDraftById(@RequestParam String id){
        CouponDeleteDTO couponDeleteDTO = couponRuleDraftService.deleteRuleDraftById(id);
        return ResponseEntity.status(200).body(couponDeleteDTO);
    }

}