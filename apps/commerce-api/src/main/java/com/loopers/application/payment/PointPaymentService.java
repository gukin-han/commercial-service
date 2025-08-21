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
public class PointPaymentService implements PaymentStrategy {

    private final PointRepository pointRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PayCommand command) {
        return command.getMethod() == PaymentMethod.POINT;
    }

    @Override
    @Transactional
    public PayResult requestPayment(PayCommand command) {
        // 포인트 조회할 수 없는 경우는 예외발생
        Point point = pointRepository.findByUserIdForUpdate(UserId.of(command.getUserId()))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트를 조회할 수 없습니다. userId : " + command.getUserId()));

        // 결제 정보 먼저 생성
        Payment payment = Payment.createPending(command.getUserId(), command.getOrderId(), command.getAmount(), PaymentMethod.POINT);
        try {
            point.deduct(command.getAmount());
            // 성공 케이스
            payment.complete();
            return PayResult.success(payment, point);
        } catch (IllegalStateException e) {
            // 포인트가 부족한 경우 -> 결제 실패 처리
            payment.fails();
            return PayResult.fail(PayResult.FailureReason.INSUFFICIENT_FUNDS);
        } finally {
            paymentRepository.save(payment);
        }

    }
}
