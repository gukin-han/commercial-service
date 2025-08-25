package com.loopers.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CouponId{

    @Column(name = "coupon_id")
    private Long value;

    public CouponId(Long value) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("CouponId는 null이 아니며 0보다 커야 합니다.");
        }
        this.value = value;
    }

    public static CouponId of(Long value) {
        return new CouponId(value);
    }
}
