package com.loopers.application.payment;

import com.loopers.infrastructure.http.payment.dto.PaymentClientV1Dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private String reason;
    private String status;

    public static Transaction from(PaymentClientV1Dto.getResponse.Transaction transaction) {
        return new Transaction(transaction.reason(), transaction.status().name());
    }
}
