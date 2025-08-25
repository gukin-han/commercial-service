package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Money;
import com.loopers.domain.user.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Embedded
    private UserId userId;

    @Embedded
    private Money totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;




    @Builder
    private Order(UserId userId, Money totalPrice, OrderStatus status) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public static Order of(UserId userId, Money totalPrice, OrderStatus status) {
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
