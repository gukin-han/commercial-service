package com.loopers.domain.coupon.policy;

import com.loopers.domain.product.Money;

public interface DiscountPolicy {

    Money calculateDiscount(Money originalPrice);

}
