package com.loopers.application.order.dto;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderId;
import lombok.Builder;
import lombok.Data;

@Data
public class PlaceOrderResult {

    private Status status;
    private OrderId orderId;
    private final String message;

    @Builder
    private PlaceOrderResult(Status status, OrderId orderId, String message) {
        this.status = status;
        this.message = message;
        this.orderId = orderId;
    }

    public static PlaceOrderResult success(Order order) {
        return PlaceOrderResult.builder()
                .status(Status.SUCCESS)
                .orderId(order.getOrderId())
                .message("")
                .build();
    }

    public static PlaceOrderResult fail(String message) {
        return PlaceOrderResult.builder()
                .status(Status.FAIL)
                .message(message)
                .build();
    }

    enum Status {
        FAIL, SUCCESS,
    }
}

