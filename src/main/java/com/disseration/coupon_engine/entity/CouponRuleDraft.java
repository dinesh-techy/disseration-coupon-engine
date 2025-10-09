package com.disseration.coupon_engine.entity;

import com.disseration.coupon_engine.dto.Rule;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "coupon_rule_draft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRuleDraft {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "rule_type", columnDefinition = "TEXT")
    private String ruleType;

    // 👇 Store entire Rule object as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_json", columnDefinition = "jsonb")
    private Rule parsedJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "missing_fields", columnDefinition = "jsonb")
    private List<String> missingFields;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Status status;

    public enum Status {
        PENDING,
        VALIDATED,
        FAILED
    }
}
