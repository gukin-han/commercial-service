package com.loopers.interfaces.api.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class PaymentV1Dto {

    @Data
    @Builder
    @AllArgsConstructor
    public static class SyncPaymentCallbackResponse {
        private String message;
    }
}
