package com.disseration.coupon_engine.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String productId;
    private String name;
    private String category;
    private String brand;
    private double price;
    private int quantity;
}
