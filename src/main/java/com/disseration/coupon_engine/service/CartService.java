package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.Cart;
import com.disseration.coupon_engine.repository.CouponRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CouponRuleRuntimeValidator couponRuleRuntimeValidator;
    private final ConflictMatrixValidator conflictMatrixValidator;

    public CartService(CouponRuleRepository couponRuleRepository, CouponRuleService couponRuleService, CouponRuleRuntimeValidator couponRuleRuntimeValidator, ConflictMatrixValidator conflictMatrixValidator) {
        this.couponRuleRuntimeValidator = couponRuleRuntimeValidator;
        this.conflictMatrixValidator = conflictMatrixValidator;
    }

    public void cartValidation(Cart cart){
        // Rule Validation - expiry and usageLimits
        Boolean coupon1RuleValidation=null;
        Boolean coupon2RuleValidation=null;
        if(cart.getCouponCode()!=null){
            coupon1RuleValidation=couponRuleRuntimeValidator.isRuleValid(cart.getCouponCode());
        }
        if (cart.getCouponCode2()!=null){
            coupon2RuleValidation = couponRuleRuntimeValidator.isRuleValid(cart.getCouponCode2());
        }
        if((cart.getCouponCode()!=null && cart.getCouponCode2()!=null) && (coupon1RuleValidation || !coupon2RuleValidation)){
            System.out.println("Coupon1 "+cart.getCouponCode()+" is "+coupon1RuleValidation);
            System.out.println("Coupon2 "+cart.getCouponCode()+" is "+coupon2RuleValidation);
        }
        // Cart Validation
        Boolean isCartValid = couponRuleRuntimeValidator.isCartValid(cart);
        if(!isCartValid){
            System.out.println("Cart is invalid for applying coupon");
        }

        // Conflict Matrix Validation
        Boolean isConflictMatric = conflictMatrixValidator.validateCouponConflicts(cart);
        if (!isConflictMatric){
            System.out.println("Conflict Matrix....");
        }
    }
}
