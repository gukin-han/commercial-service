package com.loopers.application.product.dto;

import com.loopers.interfaces.api.product.ProductV1Dto;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductDetailQuery {
    private final Long productId;

    @Builder
    private ProductDetailQuery(Long productId) {
        this.productId = productId;
    }

    public static ProductDetailQuery of(Long productId) {
        return ProductDetailQuery.builder()
                .productId(productId)
                .build();
    }

    public static ProductDetailQuery from(ProductV1Dto.GetProductByIdRequest request) {
        return ProductDetailQuery.builder()
                .productId(request.getProductId())
                .build();
    }
}
