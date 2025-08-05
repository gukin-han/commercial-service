package com.loopers.domain.coupon.policy;

import com.loopers.domain.product.Money;

public class PercentageDiscountPolicy implements DiscountPolicy {

    private final Double percent;

    public PercentageDiscountPolicy(Double percent) {
        this.percent = percent;
    }

    @Override
    public Money calculateDiscount(Money originalPrice) {
        return originalPrice.multiply(percent);
    }
}
