package com.loopers.domain.coupon;

import com.loopers.domain.user.UserId;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByIdAndCouponId(CouponId couponId, UserId userId);

    Coupon save(Coupon coupon);
}
