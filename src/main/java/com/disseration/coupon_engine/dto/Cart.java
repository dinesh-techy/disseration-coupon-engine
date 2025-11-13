package com.disseration.coupon_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private List<CartItem> items;
    private String couponCode;
    private String couponCode2;
    private String memberShipType;

    public double getTotal() {
        double cartTotal = items.stream().mapToDouble(CartItem::getPrice).sum();
        System.out.println("Cart total is "+cartTotal);
        return cartTotal;
    }
}
