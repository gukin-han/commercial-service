package com.loopers.domain.coupon.policy;

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

    public static DiscountPolicy create(CouponType type, Money amount, Percent percent) {
        Function<DiscountPolicyParams, DiscountPolicy> creator = registry.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("지원하지 않는 쿠폰 타입: " + type);
        }
        return creator.apply(new DiscountPolicyParams(amount, percent.getValue()));
    }

    public record DiscountPolicyParams(Money amount, Double percent) {}
}
