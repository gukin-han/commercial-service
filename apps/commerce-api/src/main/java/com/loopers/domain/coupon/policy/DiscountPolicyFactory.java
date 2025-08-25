package com.loopers.domain.coupon.policy;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.Percent;
import com.loopers.domain.product.Money;

import java.util.Map;
import java.util.function.Function;

public class DiscountPolicyFactory {
    private static final Map<CouponType, Function<DiscountPolicyParams, DiscountPolicy>> registry = Map.of(
            CouponType.FIXED_AMOUNT, params -> new FixedAmountDiscountPolicy(params.amount()),
            CouponType.PERCENTAGE, params -> new PercentageDiscountPolicy(params.percent()),
            CouponType.CAPPED_PERCENTAGE, params -> new CappedPercentageDiscountPolicy(params.percent(), params.amount())
    );

    public static DiscountPolicy create(Coupon coupon) {
        Function<DiscountPolicyParams, DiscountPolicy> creator = registry.get(coupon.getType());
        if (creator == null) {
            throw new IllegalArgumentException("지원하지 않는 쿠폰 타입: " + coupon.getType());
        }
        return creator.apply(new DiscountPolicyParams(coupon.getAmount(), coupon.getDiscountRate()));
    }

    public record DiscountPolicyParams(Money amount, Percent percent) {}
}
