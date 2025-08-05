package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Money;
import com.loopers.domain.user.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
@Entity
public class Coupon extends BaseEntity {

    @Enumerated(value = EnumType.STRING)
    private CouponType type;

    @Embedded
    private UserId userId;

    @Embedded
    private Money amount;

    private Double percent;

    private ZonedDateTime usedAt;

    @Builder
    private Coupon(CouponType type, UserId userId, Money amount, Double percent) {
        if (type == null || userId == null) {
            throw new IllegalArgumentException("쿠폰 타입과 유저아이디는 필수 입력값입니다.");
        }

        if (CouponType.FIXED_AMOUNT.equals(type) && amount == null) {
            throw new IllegalArgumentException("정액 쿠폰은 할인금액이 필수 입니다.");
        }

        if (CouponType.PERCENTAGE.equals(type) && (percent == null || percent <= 0)) {
            throw new IllegalArgumentException("정률 쿠폰은 할인율이 필수 입니다..");
        }

        this.type = type;
        this.userId = userId;
        this.amount = amount;
        this.percent = percent;
    }

    public static Coupon create(CouponType type, UserId userId, Money amount, Double percent) {
        return Coupon.builder()
                .type(type)
                .userId(userId)
                .amount(amount)
                .percent(percent)
                .build();
    }

    public CouponId getCouponId() {
        return getId() == null ? null : CouponId.of(getId());
    }

}
