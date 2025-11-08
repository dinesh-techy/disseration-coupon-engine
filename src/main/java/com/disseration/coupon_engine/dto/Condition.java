package com.disseration.coupon_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condition {
    private String field;       // e.g., "category", "amount", "user_type"
    private String operator;    // e.g., "EQUALS", "GREATER_THAN", "LESS_THAN"
    private String value;
}
