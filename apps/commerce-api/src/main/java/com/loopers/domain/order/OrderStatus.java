package com.loopers.domain.order;

public enum OrderStatus {
    CREATED, // 주문 생성 (결제 여부는 PaymentStatus 참조)
    SHIPPING,
    DELIVERED,
    CANCELED
}
