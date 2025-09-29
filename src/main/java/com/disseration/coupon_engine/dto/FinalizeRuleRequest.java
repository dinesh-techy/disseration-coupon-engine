package com.disseration.coupon_engine.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class FinalizeRuleRequest {
    private UUID draftId;
    private LocalDate expiryDate;   // optional
    private Integer usageLimit;     // optional
}
