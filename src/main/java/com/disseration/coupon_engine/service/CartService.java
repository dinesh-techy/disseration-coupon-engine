package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.Cart;
import com.disseration.coupon_engine.dto.Rule;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.repository.CouponRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CouponRuleRepository couponRuleRepository;
    private final CouponRuleRuntimeValidator couponRuleRuntimeValidator;

    public CartService(CouponRuleRepository couponRuleRepository, CouponRuleService couponRuleService, CouponRuleRuntimeValidator couponRuleRuntimeValidator) {
        this.couponRuleRepository = couponRuleRepository;
        this.couponRuleRuntimeValidator = couponRuleRuntimeValidator;
    }

    public void cartValidation(Cart cart){
        // Rule Validation - expiry and usageLimits
        Boolean ruleValidation = couponRuleRuntimeValidator.isRuleValid(cart.getCouponCode());
        if(!ruleValidation){
            System.out.println("Coupon is invalid");
        }
        // Cart Validation
        Boolean isCartValid = couponRuleRuntimeValidator.isCartValid(cart);
        if(!isCartValid){
            System.out.println("Cart is invalid for applying coupon");
        }

        // Conflict Matrix Validation
    }
}
