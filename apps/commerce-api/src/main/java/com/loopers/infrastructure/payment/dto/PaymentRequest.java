package com.loopers.infrastructure.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private String cardType;
    private String cardNo;
    private String amount;
    private String callbackUrl;
}
