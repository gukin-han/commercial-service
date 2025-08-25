package com.loopers.domain.coupon;

import com.loopers.domain.coupon.policy.DiscountPolicyFactory;
import com.loopers.domain.product.Money;

public class CouponDiscountCalculator {
    public Money calculateDiscountAmount(Coupon coupon, Money totalPrice) {
        return DiscountPolicyFactory.create(coupon).calculateDiscount(totalPrice);
    }
}
