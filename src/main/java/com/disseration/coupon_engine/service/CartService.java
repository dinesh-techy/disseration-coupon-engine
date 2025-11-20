package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.Cart;
import com.disseration.coupon_engine.errorHandling.CouponInvalidException;
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

    public boolean cartValidation(Cart cart){
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
            throw new CouponInvalidException("Cart is invalid for applying coupon");
        }
        Boolean isConflictMatrix=null;

        if(cart.getCouponCode()!=null && cart.getCouponCode2()!=null){
            isConflictMatrix=conflictMatrixValidator.validateCouponConflicts(cart);
        }
        return true;
    }
}
