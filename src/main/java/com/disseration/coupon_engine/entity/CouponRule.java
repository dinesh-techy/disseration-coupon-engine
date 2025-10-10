package com.disseration.coupon_engine.entity;

import com.disseration.coupon_engine.dto.Rule;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "coupon_rule")
@Data
public class CouponRule {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "rule_type", columnDefinition = "TEXT")
    private String ruleType;

    // ✅ Store full Rule as JSONB instead of String
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rule_json", columnDefinition = "jsonb")
    private Rule ruleJson;

    private LocalDate expiryDate;

    private Integer usageLimit;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Status status;

    public enum Status {
        FINALIZED,
        ACTIVE,
        EXPIRED
    }
}
