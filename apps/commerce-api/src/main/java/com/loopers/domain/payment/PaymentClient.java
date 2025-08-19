package com.loopers.domain.payment;

public interface PaymentClient {
    PayResult requestPayment(PayCommand command);
}
