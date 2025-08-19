package com.loopers.application.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentClient;
import com.loopers.domain.payment.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentStrategy {

    private final PaymentClient paymentClient;

    @Override
    public boolean supports(PayCommand command) {
        return command.getMethod() == PaymentMethod.CARD;
    }

    @Override
    public PayResult pay(PayCommand command) {
        return paymentClient.requestPayment(command);
    }
}
