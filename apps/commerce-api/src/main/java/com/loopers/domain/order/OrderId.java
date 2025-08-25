package com.loopers.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class OrderId {

    @Column(name = "order_id")
    private Long value;

    public OrderId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Order ID는 null이 아니며 0보다 커야 합니다.");
        }
        this.value = value;
    }

    public static OrderId of(Long value) {
        return new OrderId(value);
    }
}
