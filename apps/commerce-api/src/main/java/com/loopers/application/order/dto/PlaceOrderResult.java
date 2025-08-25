package com.loopers.application.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PlaceOrderResult {

    private Status status;

    private final String message;

    @Builder
    private PlaceOrderResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static PlaceOrderResult SUCCESS() {
        return PlaceOrderResult.builder()
                .status(Status.SUCCESS)
                .message("")
                .build();
    }

    public static PlaceOrderResult FAIL(String message) {
        return PlaceOrderResult.builder()
                .status(Status.FAIL)
                .message(message)
                .build();
    }

    enum Status {
        FAIL, SUCCESS,
    }
}

