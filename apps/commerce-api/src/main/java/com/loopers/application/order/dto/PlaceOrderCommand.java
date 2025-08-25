package com.loopers.application.order.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderCommand {

    private final Cart cart;

    private final Long userId;


    @Builder
    private PlaceOrderCommand(Cart cart, Long userId) {
        this.cart = cart;
        this.userId = userId;
    }

    public static PlaceOrderCommand of(Cart cart, Long userId) {
        return PlaceOrderCommand.builder()
                .cart(cart)
                .userId(userId)
                .build();
    }
}
