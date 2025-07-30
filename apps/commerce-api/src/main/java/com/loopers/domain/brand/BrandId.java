package com.loopers.domain.brand;

public record BrandId (Long value) {

    public BrandId {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Brand ID는 null이 아니며 0보다 커야 합니다.");
        }
    }

    public static BrandId of(Long value) {
        return new BrandId(value);
    }
}
