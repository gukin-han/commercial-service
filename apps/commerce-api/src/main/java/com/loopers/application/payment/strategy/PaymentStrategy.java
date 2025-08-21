package com.loopers.application.payment.strategy;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;

public interface PaymentStrategy {
    boolean supports(PayCommand command);
    PayResult requestPayment(PayCommand command);
}
