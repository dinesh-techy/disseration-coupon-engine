package com.disseration.coupon_engine.service;

import com.disseration.coupon_engine.dto.Cart;
import com.disseration.coupon_engine.dto.Rule;
import com.disseration.coupon_engine.entity.CouponRule;
import com.disseration.coupon_engine.enumm.CouponScope;
import com.disseration.coupon_engine.errorHandling.CouponInvalidException;
import com.disseration.coupon_engine.repository.CouponRuleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConflictMatrixValidator {

    private final CouponRuleRepository couponRuleRepository;

    public ConflictMatrixValidator(CouponRuleRepository couponRuleRepository) {
        this.couponRuleRepository = couponRuleRepository;
    }

    /**
     * Validates that applied coupons are compatible:
     * - Only one CART_LEVEL coupon can exist.
     * - CATEGORY_LEVEL coupons must target different categories.
     */
    public boolean validateCouponConflicts(Cart cart) {
        Rule couponRule1 = null;
        Rule couponRule2 = null;

        // Load coupon 1 safely
        if (cart.getCouponCode() != null) {
            CouponRule coupon1 = couponRuleRepository.findByCouponCode(cart.getCouponCode());
            if (coupon1 != null) {
                couponRule1 = coupon1.getRuleJson();
            }
        }

        // Load coupon 2 safely
        if (cart.getCouponCode2() != null) {
            CouponRule coupon2 = couponRuleRepository.findByCouponCode(cart.getCouponCode2());
            if (coupon2 != null) {
                couponRule2 = coupon2.getRuleJson();
            }
        }


        // Check stackability
        if (
                (couponRule1 != null && couponRule1.getStackable() != null && !couponRule1.getStackable() && couponRule2 != null)
                        ||
                        (couponRule2 != null && couponRule2.getStackable() != null && !couponRule2.getStackable() && couponRule1 != null)
        ) {
            System.out.println("❌ Conflict: Coupon Stackability is not allowed apply only 1 coupon");
            throw new CouponInvalidException("❌ Conflict: Coupon Stackability is not allowed apply only 1 coupon");
        }

        // Stackability check for CouponType Level
       if((!couponRule1.getCouponRule().isCategoryLevelStackability() && couponRule2.getCouponRule().getScope().toString().equals("CATEGORY_LEVEL")) || (!couponRule2.getCouponRule().isCategoryLevelStackability()&&couponRule1.getCouponRule().getScope().toString().equals("CATEGORY_LEVEL"))) {
           System.out.println("❌ Conflict: Coupon Category Stackability is not allowed!");
           throw new CouponInvalidException("❌ Conflict: Coupon Category Stackability is not allowed!");
       }
        else if((!couponRule1.getCouponRule().isCartLevelStackability() && couponRule2.getCouponRule().getScope().toString().equals("CART_LEVEL")) || (!couponRule2.getCouponRule().isCartLevelStackability()&&couponRule1.getCouponRule().getScope().toString().equals("CART_LEVEL"))) {
            System.out.println("❌ Conflict: Coupon Cart Stackability is not allowed!");
            throw new CouponInvalidException("❌ Conflict: Coupon Cart Stackability is not allowed!");
        }
        // Add the CouponRule in List
        List<com.disseration.coupon_engine.dto.CouponRule> appliedCoupons = new ArrayList<>();
        appliedCoupons.add(couponRule1.getCouponRule());
        appliedCoupons.add(couponRule2.getCouponRule());
        Set<String> categorySet = new HashSet<>();
        boolean hasCartLevel = false;

        for (com.disseration.coupon_engine.dto.CouponRule coupon : appliedCoupons) {
            CouponScope scope = coupon.getScope();
            // 1️⃣ CATEGORY_LEVEL same category check
            if (scope == CouponScope.CATEGORY_LEVEL) {
                String category = coupon.getTargetCategory().toLowerCase();
                if (categorySet.contains(category)) {
                    System.out.printf("❌ Conflict: Multiple coupons for same category '%s'.%n", category);
                    throw new CouponInvalidException("❌ Conflict: Multiple coupons for same category '%s'.%n"+ category);
                }
                categorySet.add(category);
            }
        }

        System.out.println("✅ Coupons are compatible (Category/Cart matrix passed)");
        return true;
    }

}
