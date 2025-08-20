package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.Money;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@Table(name = "payments")
@Entity
public class Payment extends BaseEntity {
    private Long orderId;
    private Long userId;
    private PaymentMethod method;
    private PaymentStatus status;

    private ZonedDateTime completedAt;
    private ZonedDateTime failedAt;
    private ZonedDateTime canceledAt;

    @Builder
    private Payment(Long orderId, Long userId, PaymentMethod method, PaymentStatus status, ZonedDateTime completedAt, ZonedDateTime failedAt, ZonedDateTime canceledAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.method = method;
        this.status = status;
        this.completedAt = completedAt;
        this.failedAt = failedAt;
        this.canceledAt = canceledAt;
    }

    public static Payment createPending(Long userId, Long orderId, Money amount, PaymentMethod method) {
        return Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .method(method)
                .status(PaymentStatus.PENDING)
                .build();
    }

    public static Payment createFailed(Long userId, Long orderId, Money amount, PaymentMethod method) {
        return Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .method(method)
                .status(PaymentStatus.FAILED)
                .build();
    }

    public void fails() {
        this.status = PaymentStatus.FAILED;
    }
}
