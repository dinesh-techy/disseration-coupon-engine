package com.disseration.coupon_engine.errorHandling;

public class CouponTypeNotFoundException extends RuntimeException{
    public CouponTypeNotFoundException(String message) {
        super(message);
    }
}
