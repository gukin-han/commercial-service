package com.loopers.domain.product;

import com.loopers.domain.brand.BrandId;
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
public class ProductId{

    @Column(name = "product_id")
    private Long value;

    public ProductId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Product ID는 null이 아니며 0보다 커야 합니다.");
        }
        this.value = value;
    }

    public static ProductId of(Long value) {
        return new ProductId(value);
    }

}
