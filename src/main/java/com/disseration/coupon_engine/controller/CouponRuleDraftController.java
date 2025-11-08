package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.CouponDeleteDTO;
import com.disseration.coupon_engine.dto.RuleDraft;
import com.disseration.coupon_engine.entity.CouponRuleDraft;
import com.disseration.coupon_engine.entity.GenerateNewRule;
import com.disseration.coupon_engine.service.CouponRuleDraftService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/draftRule")
@RestController
@Tag(name = "Coupon Rule Draft", description = "APIs for Coupon Rule Draft")
public class CouponRuleDraftController {

    private final CouponRuleDraftService couponRuleDraftService;

    public CouponRuleDraftController(CouponRuleDraftService couponRuleDraftService) {
        this.couponRuleDraftService = couponRuleDraftService;
    }

    @PostMapping
    @Operation(summary = "Create a new coupon draft", description = "Add a new coupon rule draft based on the input prompt")
    public ResponseEntity<RuleDraft> generateDraftRule(@RequestBody GenerateNewRule generateNewRule) throws JsonProcessingException {
        RuleDraft couponRule = couponRuleDraftService.generateCouponDraftRule(generateNewRule.getNewRule());
        return ResponseEntity.status(201).body(couponRule);
    }

    @GetMapping
    @Operation(summary = "Retrieve the coupon draft", description = "Retrieve the generated coupon draft using the draftId")
    public ResponseEntity<CouponRuleDraft> getRuleDraftById(@RequestParam String id){
        CouponRuleDraft couponRuleDraft = couponRuleDraftService.getRuleDraftById(id);
        return ResponseEntity.status(200).body(couponRuleDraft);
    }

    @DeleteMapping
    @Operation(summary = "Delete the coupon draft", description = "Delete the generated coupon draft using the draftId")
    public ResponseEntity<CouponDeleteDTO> deleteRuleDraftById(@RequestParam String id){
        CouponDeleteDTO couponDeleteDTO = couponRuleDraftService.deleteRuleDraftById(id);
        return ResponseEntity.status(200).body(couponDeleteDTO);
    }

}