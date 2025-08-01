package com.loopers.domain.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductId{

    @Column(name = "stock_quantity")
    private Long value;

    public ProductId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Product ID는 null이 아니며 0보다 커야 합니다.");
        }
    }

    public static ProductId of(Long value) {
        return new ProductId(value);
    }
}
