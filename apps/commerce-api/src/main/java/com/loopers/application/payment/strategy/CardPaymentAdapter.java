package com.loopers.application.payment.strategy;

import com.loopers.domain.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentStrategy {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;


    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.CARD;
    }

    @Override
    public PayResult requestPayment(PayCommand command) {
        // 결제 정보 먼저 생성
        Payment payment = Payment.createPending(command.getUserId(), command.getOrderId(), command.getAmount(), PaymentMethod.CARD);
        try {
            // 결제 요청
            return paymentClient.requestPayment(command);
        } catch (Throwable e) {
            // 에러 발생시 결제 실패 처리
            payment.fails();
            return PayResult.fail(PayResult.FailureReason.NETWORK_ERROR);
        } finally {
            // 결제 정보 저장
            paymentRepository.save(payment);
        }
    }
}
