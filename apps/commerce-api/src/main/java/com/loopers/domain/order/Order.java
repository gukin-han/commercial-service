package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.CouponId;
import com.loopers.domain.product.Money;
import com.loopers.domain.user.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Embedded
    private UserId userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Money totalPrice;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "discount_amount"))
    private Money discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Embedded
    private CouponId couponId;

    @Builder
    private Order(UserId userId, Money totalPrice, Money discountAmount, OrderStatus status, CouponId couponId) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.discountAmount = discountAmount;
        this.status = status;
        this.couponId = couponId;
    }

    public static Order of(UserId userId, Money totalPrice, Money discountAmount, OrderStatus status) {
        return Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .status(status)
                .build();
    }



    public OrderId getOrderId() {
        return getId() == null ? null : OrderId.of(getId());
    }
}
