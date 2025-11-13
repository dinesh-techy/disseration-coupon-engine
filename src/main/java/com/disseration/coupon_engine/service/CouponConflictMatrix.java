package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.enumm.CouponScope;
import org.springframework.stereotype.Service;

@Service
public class CouponConflictMatrix {
    /**
     * Determines if two coupon scopes can coexist.
     * Rules:
     * - CART_LEVEL coupon cannot coexist with any other coupon
     * - CATEGORY_LEVEL coupons can coexist only if targeting different categories
     */

    public static boolean canCoexist(CouponScope s1, CouponScope s2) {
        // Case 1: If any coupon is CART_LEVEL → not allowed to combine
        if (s1 == CouponScope.CART_LEVEL || s2 == CouponScope.CART_LEVEL) {
            return false;
        }

        // Case 2: CATEGORY_LEVEL + CATEGORY_LEVEL (allowed, but categories validated separately)
        return true;
    }
}
