package com.loopers.application.coupon;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponId;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.user.UserId;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponQueryService {

    private final CouponRepository couponRepository;

    public Coupon getCouponByCouponIdAndUserId(CouponId couponId, UserId userId) {
        return couponRepository.findByIdAndCouponId(couponId, userId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
