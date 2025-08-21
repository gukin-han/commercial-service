package com.loopers.application.payment.strategy;

import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {

    private final List<PaymentStrategy> strategies;

    public PayResult requestPayment(PayCommand command) {
        PaymentStrategy strategy = strategies.stream()
                .filter(s -> s.supports(command))
                .findFirst()
                .orElseThrow(() -> new CoreException(ErrorType.CONFLICT,"지원하지 않는 결제수단: " + command.getMethod()));
        return strategy.requestPayment(command);
    }
}
