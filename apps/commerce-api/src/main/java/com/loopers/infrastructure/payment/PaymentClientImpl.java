package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentClient;
import com.loopers.infrastructure.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @Override
    public PayResult requestPayment(PayCommand command) {
        PaymentResponse response = paymentFeignClient.requestPayment(command.getLoginId(), command.toRequest());
        return PayResult.from(response);
    }
}
