package com.loopers.application.payment.strategy;

import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentStrategyRouter {

    private final List<PaymentStrategy> strategies;

    public PaymentStrategy requestPayment(PaymentMethod method) {
        return strategies.stream()
                .filter(s -> s.supports(method))
                .findFirst()
                .orElseThrow(() -> new CoreException(ErrorType.CONFLICT,"지원하지 않는 결제수단: " + method));
    }
}
