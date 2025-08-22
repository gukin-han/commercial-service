package com.loopers.domain.payment;

import com.loopers.application.payment.Transaction;

import java.util.List;

public interface PaymentClient {
    PayResult requestPayment(PayCommand command);

    List<Transaction> getTransactionByOrderId(Long orderId);
}
