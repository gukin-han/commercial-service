package com.loopers.domain.like;

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
public class ProductLikeId {

    @Column(name = "product_like_id")
    private Long value;

    public ProductLikeId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ProductLike ID는 null이 아니며 0보다 커야 합니다.");
        }
        this.value = value;
    }

    public static ProductLikeId of(Long value) {
        return new ProductLikeId(value);
    }
}
