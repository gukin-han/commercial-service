package com.loopers.infrastructure.payment.dto;

public class PaymentResponse {
    private Meta meta;
    private Data data;



    public static class Meta {
        Result result;
        String errorCode;
        String message;
    }

    public enum Result {
        SUCCESS, FAIL;

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }

    public static class Data {
        private String transactionKey;
        private Status status;
    }

    public enum Status {
        PENDING
    }
}
