package com.disseration.coupon_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Condition {
    private String field;       // e.g., "category", "amount", "user_type"
    private String operator;    // e.g., "EQUALS", "GREATER_THAN", "LESS_THAN"
    private String value;
}


@Data
public class Rule {
    private String type;
    private String value;
    private List<Condition> conditions;
    private LocalDate expiryDate;
    private Integer usageLimit;
    private Boolean stackable;

    // This setter handles the single "condition" field from JSON
    @JsonProperty("condition")
    private void unpackCondition(Condition condition) {
        if (condition != null) {
            this.conditions = List.of(condition); // wrap single object in a list
        } else {
            this.conditions = new ArrayList<>();
        }
    }// e.g., "electronics", "1000"
}