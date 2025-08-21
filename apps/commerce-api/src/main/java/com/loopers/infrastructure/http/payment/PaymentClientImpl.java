package com.loopers.infrastructure.http.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentClient;
import com.loopers.infrastructure.http.HttpResponse;
import com.loopers.infrastructure.http.payment.dto.PaymentClientV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @Override
    public PayResult requestPayment(PayCommand command) {
        HttpResponse<PaymentClientV1Dto.PaymentResponse> response = paymentFeignClient.requestPayment(
                command.getLoginId(),
                PaymentClientV1Dto.PaymentRequest.from(command)
        );
        return response.toPayResult();
    }
}
