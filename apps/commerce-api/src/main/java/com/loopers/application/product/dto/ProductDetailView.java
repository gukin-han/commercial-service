package com.loopers.application.product.dto;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductDetailView {

    private Long stockQuantity;

    private long likeCount;

    private ProductStatus productStatus;

    private String productName;

    private String brandName;

    @Builder
    private ProductDetailView(Long stockQuantity, long likeCount, ProductStatus productStatus, String productName, String brandName) {
        this.stockQuantity = stockQuantity;
        this.likeCount = likeCount;
        this.productStatus = productStatus;
        this.productName = productName;
        this.brandName = brandName;
    }

    public static ProductDetailView create(Product product, Brand brand) {
        return ProductDetailView.builder()
                .productName(product.getName())
                .likeCount(product.getLikeCount())
                .stockQuantity(product.getLikeCount())
                .productStatus(product.getStatus())
                .brandName(brand.getName())
                .build();
    }
}
