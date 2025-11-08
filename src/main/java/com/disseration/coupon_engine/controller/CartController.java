package com.disseration.coupon_engine.controller;

import com.disseration.coupon_engine.dto.Cart;
import com.disseration.coupon_engine.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("cart")
@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("validate")
    public ResponseEntity<String> validateCart(@RequestBody Cart cart){
        cartService.cartValidation(cart);
        return ResponseEntity.status(200).body("Cart validated successfully");
    }
}
