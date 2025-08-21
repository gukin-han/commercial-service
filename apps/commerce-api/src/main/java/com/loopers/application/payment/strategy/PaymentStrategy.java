package com.loopers.application.payment.strategy;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentMethod;

public interface PaymentStrategy {
    boolean supports(PaymentMethod command);
    PayResult requestPayment(PayCommand command);
}
