package com.loopers.domain.coupon.policy;

import com.loopers.domain.product.Money;

public class CappedPercentageDiscountPolicy implements DiscountPolicy {

    private final Money discountAmount;
    private final Double discountPercent;

    public CappedPercentageDiscountPolicy(Double discountPercent, Money discountAmount) {
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
    }

    @Override
    public Money calculateDiscount(Money originalPrice) {
        Money percentageDiscount = originalPrice.multiply(discountPercent);

        if (percentageDiscount.getValue().compareTo(discountAmount.getValue()) > 0) {
            return discountAmount;
        }

        return percentageDiscount;
    }
}
