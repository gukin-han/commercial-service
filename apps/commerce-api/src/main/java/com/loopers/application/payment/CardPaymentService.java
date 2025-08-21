package com.loopers.application.payment;

import com.loopers.domain.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentService implements PaymentStrategy {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;


    @Override
    public boolean supports(PayCommand command) {
        return command.getMethod() == PaymentMethod.CARD;
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
