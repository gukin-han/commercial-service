package com.loopers.domain.coupon;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponId;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon getCouponByCouponIdAndUserId(CouponId couponId, UserId userId) {
        Coupon coupon = couponRepository.findByIdAndCouponId(couponId, userId)
                .orElse(null);
        if (coupon != null && coupon.isUsed()) {
            throw new IllegalStateException("쿠폰은 한 번만 사용할 수 있습니다.");
        }
        return coupon;
    }
}
