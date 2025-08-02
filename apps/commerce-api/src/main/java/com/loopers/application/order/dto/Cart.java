package com.loopers.application.order.dto;

import com.loopers.domain.order.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class Cart {

    private final List<CartItem> cartItems;

    @Builder
    public Cart(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public static Cart from(List<CartItem> cartItems) {
        return Cart.builder()
                .cartItems(cartItems)
                .build();
    }

}
