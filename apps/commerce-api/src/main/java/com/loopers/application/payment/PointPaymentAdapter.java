package com.loopers.application.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.payment.PaymentMethod;
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

    @Override
    public boolean supports(PayCommand command) {
        return command.getMethod() == PaymentMethod.POINT;
    }

    @Override
    @Transactional
    public PayResult pay(PayCommand command) {
        Point point = pointRepository.findByUserIdForUpdate(UserId.of(command.getUserId()))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트를 조회할 수 없습니다. userId : " + command.getUserId()));
        point.deduct(command.getAmount());
        return null;
    }
}
