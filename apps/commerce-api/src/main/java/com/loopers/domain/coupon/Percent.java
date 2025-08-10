package com.loopers.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Percent {

    @Column(name = "discount_rate")
    private Double value;

    private Percent(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("null은 할인율은 될 수 없습니다.");
        }

        if (value.isNaN()) {
            throw new IllegalArgumentException("NaN은 할인율은 될 수 없습니다.");
        }

        if (value <= 0.0 || value > 1.0) {
            throw new IllegalArgumentException("할인율은 0보다 크고 1 이하이어야 합니다.");
        }

        this.value = value;
    }

    public static Percent of(Double value) {
        return new Percent(value);
    }

}
