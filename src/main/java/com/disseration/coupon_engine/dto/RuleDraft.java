package com.disseration.coupon_engine.dto;
import lombok.Data;
import java.util.List;

@Data
public class RuleDraft {
    private Rule rule;
    private List<String> missingFields;

    public RuleDraft(Rule rule, List<String> missingFields) {
        this.rule = rule;
        this.missingFields = missingFields;
    }
}
