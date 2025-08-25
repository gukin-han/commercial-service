package com.loopers.domain.point;

import com.loopers.domain.product.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.Getter;

@Getter
public class PointCharge {

    private final Money amount;

    public PointCharge(Money amount) {

        if (!amount.isPositive()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");
        }

        this.amount = amount;
    }
}
