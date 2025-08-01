package com.loopers.application.product.dto;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductStatus;

public record ProductDetailView(
        long stockQuantity,
        long likeCount,
        ProductStatus productStatus,
        String productName,
        String brandName
) {
    public static ProductDetailView create(Product product, Brand brand) {
        return new ProductDetailView(
                product.getStock().getQuantity(),
                product.getLikeCount(),
                product.getStatus(),
                product.getName(),
                brand.getName()
        );
    }
}

