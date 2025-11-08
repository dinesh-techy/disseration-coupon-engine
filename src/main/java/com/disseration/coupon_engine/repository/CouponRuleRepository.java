package com.disseration.coupon_engine.repository;

import com.disseration.coupon_engine.entity.CouponRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CouponRuleRepository extends JpaRepository<CouponRule, UUID> {
    List<CouponRule> findByRuleType(String couponType);

    @Query(
            value = "SELECT * FROM coupon_rule r WHERE r.rule_json ->> 'couponCode' = :couponCode",
            nativeQuery = true
    )
    CouponRule findByCouponCode(@Param("couponCode") String couponCode);
}
