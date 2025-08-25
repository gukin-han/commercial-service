package com.loopers.application.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CartItem {

    private final Long productId;

    private final Long quantity;

    @Builder
    private CartItem(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CartItem of(Long productId, Long quantity) {
        return CartItem.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
