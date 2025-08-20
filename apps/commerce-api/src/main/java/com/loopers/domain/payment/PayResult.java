package com.loopers.domain.payment;

import com.loopers.domain.point.Point;
import com.loopers.infrastructure.payment.dto.PaymentResponse;
import lombok.Builder;
import lombok.Data;

@Data
public class PayResult {

    private Status status;
    private FailureReason reason;

    @Builder
    private PayResult(Status status, FailureReason reason) {
        this.status = status;
        this.reason = reason;
    }

    public static PayResult from(PaymentResponse response) {
        return null;
    }

    public static PayResult fail(FailureReason reason) {
        return PayResult.builder()
                .status(Status.FAILED)
                .reason(reason)
                .build();
    }

    public static PayResult success(Payment payment, Point point) {
        return PayResult.builder()
                .status(Status.SUCCESS)
                .build();
    }

    public enum Status {
        SUCCESS, // 결제 성공
        FAILED,  // 결제 실패
        PENDING  // 결제 대기 중
    }

    public enum FailureReason {
        INSUFFICIENT_FUNDS, // 잔액 부족
        INVALID_PAYMENT_METHOD, // 잘못된 결제 수단
        NETWORK_ERROR, // 네트워크 오류
        UNKNOWN_ERROR // 알 수 없는 오류
    }
}

