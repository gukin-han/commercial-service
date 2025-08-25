package com.loopers.domain.coupon.policy;

import com.loopers.domain.product.Money;

public class FixedAmountDiscountPolicy implements DiscountPolicy {

    private final Money amount;

    public FixedAmountDiscountPolicy(Money amount) {
        this.amount = amount;
    }

    @Override
    public Money calculateDiscount(Money originalPrice) {
        return amount;
    }
}
