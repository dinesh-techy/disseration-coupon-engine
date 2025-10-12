package com.disseration.coupon_engine.errorHandling;

public class CouponFinalizeException extends RuntimeException{
    public CouponFinalizeException(String message) {
        super(message);
    }
}
