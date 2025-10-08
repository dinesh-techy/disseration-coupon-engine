package com.disseration.coupon_engine.repository;

import com.disseration.coupon_engine.entity.CouponRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CouponRuleRepository extends JpaRepository<CouponRule, UUID> {
    List<CouponRule> findByRuleType(String couponType);
}
