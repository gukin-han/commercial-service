package com.loopers.application.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PlaceOrderCommand {

    private final Cart cart;

    private final Long userId;

    private final Long couponId;


    @Builder
    private PlaceOrderCommand(Cart cart, Long userId, Long couponId) {
        this.cart = cart;
        this.userId = userId;
        this.couponId = couponId;
    }

    public static PlaceOrderCommand of(Cart cart, Long userId, Long couponId) {
        return PlaceOrderCommand.builder()
                .cart(cart)
                .userId(userId)
                .couponId(couponId)
                .build();
    }
}
