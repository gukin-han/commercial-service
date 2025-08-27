package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByIdAndCouponId(Long couponId, Long userId);

    Coupon save(Coupon coupon);
}
