package com.loopers.infrastructure.http;

import com.loopers.domain.payment.PayResult;

public record HttpResponse<T>(Metadata meta, T data) {
    public PayResult toPayResult() {
        return null;
    }

    public record Metadata(Result result, String errorCode, String message) {
        public enum Result {
            SUCCESS, FAIL
        }
    }
}
