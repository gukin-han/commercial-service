package com.loopers.infrastructure.http.payment.dto;

import com.loopers.domain.payment.PayCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;

public class PaymentClientV1Dto {

    public record PaymentResponse(
            String transactionKey,
            Status status
    ) {
        public enum Status {
            PENDING
        }
    }


    @Builder
    public record PaymentRequest(
            String orderId,
            String cardType,
            String cardNo,
            String amount,
            String callbackUrl
    ) {
        public static PaymentRequest from(PayCommand command) {
            return PaymentRequest.builder()
                    .orderId(command.getOrderId().toString())
                    .cardType(command.getCardType())
                    .cardNo(command.getCardNo())
                    .amount(command.getAmount().toString())
                    .callbackUrl(command.getCallbackUrl())
                    .build();
        }
    }
}
