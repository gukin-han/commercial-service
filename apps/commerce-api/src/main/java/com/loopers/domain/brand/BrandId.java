package com.loopers.domain.brand;

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
public class BrandId{

    @Column(name = "brand_id")
    private Long value;

    public BrandId(Long value) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Brand ID는 null이 아니며 0보다 커야 합니다.");
        }
        this.value = value;
    }

    public static BrandId of(Long value) {
        return new BrandId(value);
    }
}
