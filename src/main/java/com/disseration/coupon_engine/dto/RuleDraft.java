package com.disseration.coupon_engine.dto;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class RuleDraft {
    private UUID ruleDraftId;
    private Rule rule;
    private List<String> missingFields;

    public RuleDraft(Rule rule, List<String> missingFields) {
        this.rule = rule;
        this.missingFields = missingFields;
    }
}
