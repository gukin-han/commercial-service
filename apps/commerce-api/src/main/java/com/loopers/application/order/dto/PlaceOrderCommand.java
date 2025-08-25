package com.loopers.application.order.dto;

import com.loopers.domain.payment.PaymentMethod;
import lombok.Builder;
import lombok.Data;

@Data
public class PlaceOrderCommand {

    private final Cart cart;

    private final Long userId;

    private final Long couponId;

    private final PaymentMethod paymentMethod;


    @Builder
    private PlaceOrderCommand(Cart cart, Long userId, Long couponId, PaymentMethod paymentMethod) {
        this.cart = cart;
        this.userId = userId;
        this.couponId = couponId;
        this.paymentMethod = paymentMethod;
    }

    public static PlaceOrderCommand of(Cart cart, Long userId, Long couponId, PaymentMethod paymentMethod) {
        return PlaceOrderCommand.builder()
                .cart(cart)
                .userId(userId)
                .couponId(couponId)
                .paymentMethod(paymentMethod)
                .build();
    }
}
