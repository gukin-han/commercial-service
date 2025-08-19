package com.loopers.domain.payment;

import com.loopers.domain.product.Money;
import com.loopers.infrastructure.payment.dto.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PayCommand {
    private Long userId;
    private String loginId;
    private Money amount;
    private PaymentMethod method;

    public PaymentRequest toRequest() {
        return null;
    }
}
