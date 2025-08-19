package com.loopers.domain.payment;

import java.time.ZonedDateTime;

public class Payment {
    private Long orderId;
    private Long userId;
    private PaymentMethod method;
    private PaymentStatus status;

    private ZonedDateTime completedAt;
    private ZonedDateTime failedAt;
    private ZonedDateTime canceledAt;
}
