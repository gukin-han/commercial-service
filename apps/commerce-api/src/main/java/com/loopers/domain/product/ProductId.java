package com.loopers.domain.product;

public record ProductId(Long value) {

    public ProductId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Product ID는 null이 아니며 0보다 커야 합니다.");
        }
    }

    public static ProductId of(Long value) {
        return new ProductId(value);
    }
}
