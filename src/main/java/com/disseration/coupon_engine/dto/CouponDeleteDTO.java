package com.disseration.coupon_engine.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CouponDeleteDTO {
    private UUID couponId;
    private String message;

    public CouponDeleteDTO(UUID couponRuleId, String s) {
        this.couponId=couponRuleId;
        this.message=s;
    }
}
