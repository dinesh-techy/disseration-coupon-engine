package com.disseration.coupon_engine.dto;
import com.disseration.coupon_engine.enumm.CouponScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponRule {
    private String name;
    private CouponScope scope;
    private String targetCategory; // null for cart-level coupons
}

