package com.loopers.domain.coupon.policy;

import com.loopers.domain.coupon.Percent;
import com.loopers.domain.product.Money;

public class PercentageDiscountPolicy implements DiscountPolicy {

    private final Percent percent;

    public PercentageDiscountPolicy(Percent percent) {
        this.percent = percent;
    }

    @Override
    public Money calculateDiscount(Money originalPrice) {
        return originalPrice.multiply(percent.getValue());
    }
}
