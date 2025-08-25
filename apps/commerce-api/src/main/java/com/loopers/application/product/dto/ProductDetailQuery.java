package com.loopers.application.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ProductDetailQuery {
    private final Long productId;
    private final Long brandId;

    @Builder
    private ProductDetailQuery(Long productId, Long brandId) {
        this.productId = productId;
        this.brandId = brandId;
    }

    public static ProductDetailQuery of(Long productId, Long brandId) {
        return ProductDetailQuery.builder()
                .productId(productId)
                .brandId(brandId)
                .build();
    }
}
