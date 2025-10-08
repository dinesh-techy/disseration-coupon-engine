package com.disseration.coupon_engine.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "coupon_rule")
@Data
public class CouponRule {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "rule_type", columnDefinition = "TEXT")
    private String ruleType;

    @Column(name = "rule_json", columnDefinition = "TEXT")
    private String ruleJson;

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
