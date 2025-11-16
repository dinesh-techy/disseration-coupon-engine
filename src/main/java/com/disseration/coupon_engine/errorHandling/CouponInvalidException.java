package com.disseration.coupon_engine.errorHandling;

public class CouponInvalidException extends RuntimeException{
    public CouponInvalidException(String message) {
        super(message);
    }
}