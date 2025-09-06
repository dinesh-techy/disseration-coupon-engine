package com.disseration.coupon_engine.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Rule {
    private String type;
    private String value;
    private List<String> conditions;
    private LocalDate expiryDate;
    private Integer usageLimit;
    private Boolean stackable;
}