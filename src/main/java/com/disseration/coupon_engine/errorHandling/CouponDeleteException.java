package com.disseration.coupon_engine.errorHandling;

public class CouponDeleteException extends RuntimeException{
    public CouponDeleteException(String message) {
        super(message);
    }
}
