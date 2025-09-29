package com.disseration.coupon_engine.repository;

import com.disseration.coupon_engine.entity.CouponRuleDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponRuleDraftRepository extends JpaRepository<CouponRuleDraft,UUID> {
}
