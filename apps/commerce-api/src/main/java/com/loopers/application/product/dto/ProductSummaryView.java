package com.loopers.application.product.dto;

import com.loopers.domain.product.ProductStatus;

public record ProductSummaryView(
        long stockQuantity,
        long likeCount,
        ProductStatus productStatus,
        String productName,
        String brandName
) {
}
