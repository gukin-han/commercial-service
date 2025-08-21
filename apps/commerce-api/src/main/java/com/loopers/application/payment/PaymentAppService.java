package com.loopers.application.payment;

import com.loopers.application.payment.dto.SyncPaymentCommand;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAppService {

    private final PaymentRepository paymentRepository;

    public void syncPaymentCallback(SyncPaymentCommand command) {
        // 결제 상태 동기화 로직
        // 1. 주문 ID로 결제 정보 조회
        Payment payment = paymentRepository.findByOrderId(command.getOrderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        // 2. 결제 상태 업데이트
//        payment.syncStatus(command.getStatus());
    }
}
