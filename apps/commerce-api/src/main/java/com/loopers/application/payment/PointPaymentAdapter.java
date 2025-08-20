package com.loopers.application.payment;

import com.loopers.domain.payment.*;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointPaymentAdapter implements PaymentStrategy {

    private final PointRepository pointRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PayCommand command) {
        return command.getMethod() == PaymentMethod.POINT;
    }

    @Override
    @Transactional
    public PayResult requestPayment(PayCommand command) {
        Point point = pointRepository.findByUserIdForUpdate(UserId.of(command.getUserId()))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트를 조회할 수 없습니다. userId : " + command.getUserId()));
        try {
            point.deduct(command.getAmount());
            Payment payment = Payment.createPending(command.getUserId(), command.getOrderId(), command.getAmount(), PaymentMethod.POINT);
            payment = paymentRepository.save(payment);
            return PayResult.success(payment, point);
        } catch (RuntimeException e) {
            // 포인트가 부족한 경우 -> 결제 실패 처리
            Payment payment = Payment.createFailed(command.getUserId(), command.getOrderId(), command.getAmount(), PaymentMethod.POINT);
            payment = paymentRepository.save(payment);
            return PayResult.fail(PayResult.FailureReason.INSUFFICIENT_FUNDS);
        }
    }
}
