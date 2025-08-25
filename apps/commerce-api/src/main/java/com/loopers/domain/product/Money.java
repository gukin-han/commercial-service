package com.loopers.domain.product;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

import lombok.*;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private BigDecimal value;

    @Builder
    private Money(BigDecimal value) {
        this.value = value;
    }

    public static Money of(BigDecimal value) {
        return Money.builder()
            .value(value)
            .build();
    }

    public static Money of(long value) {
        return Money.builder()
            .value(BigDecimal.valueOf(value))
            .build();
    }

    public boolean isNegative() {
        return this.value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    public Money add(Money money) {
        return new Money(this.value.add(money.getValue()));
    }

    public Money subtract(Money money) {
        return new Money(this.value.subtract(money.getValue()));
    }

    public Money multiply(double multiplicand) {
        return new Money(this.value.multiply(BigDecimal.valueOf(multiplicand)));
    }

    public boolean isLessThan(Money other) {
        return this.value.compareTo(other.value) < 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.value.compareTo(other.value) >= 0;
    }

    public static final Money ZERO = Money.of(0L);
}
