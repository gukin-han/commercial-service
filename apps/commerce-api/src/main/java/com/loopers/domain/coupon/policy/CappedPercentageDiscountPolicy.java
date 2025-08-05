package com.loopers.domain.coupon.policy;

import com.loopers.domain.coupon.Percent;
import com.loopers.domain.product.Money;

public class CappedPercentageDiscountPolicy implements DiscountPolicy {

    private final Money discountAmount;
    private final Percent discountPercent;

    public CappedPercentageDiscountPolicy(Percent discountPercent, Money discountAmount) {
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
    }

    @Override
    public Money calculateDiscount(Money originalPrice) {
        Money percentageDiscount = originalPrice.multiply(discountPercent.getValue());

        if (percentageDiscount.getValue().compareTo(discountAmount.getValue()) > 0) {
            return discountAmount;
        }

        return percentageDiscount;
    }
}
