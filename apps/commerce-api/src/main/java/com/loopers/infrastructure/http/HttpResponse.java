package com.loopers.infrastructure.http;

import com.loopers.domain.payment.PayResult;

public record HttpResponse<T>(Meta meta, T data) {
    public PayResult toSuccessPayResult() {

        return PayResult.builder()
                .status(PayResult.Status.SUCCESS)
                .build();
    }

    public record Meta(Result result, String errorCode, String message) {
        public enum Result {
            SUCCESS, FAIL
        }
    }

}
