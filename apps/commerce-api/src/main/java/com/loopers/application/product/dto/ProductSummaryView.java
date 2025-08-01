package com.loopers.application.product.dto;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandId;
import com.loopers.domain.product.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductSummaryView {

    // Product
    private final ProductId productId;

    private final Stock stock;

    private final long likeCount;

    private final ProductStatus status;

    private final String productName;

    private final Money price;

    // Brand
    private final BrandId brandId;

    private final String brandName;

    @Builder
    private ProductSummaryView(ProductId productId, Stock stock, long likeCount, ProductStatus status, String productName, Money price, BrandId brandId, String brandName) {
        this.productId = productId;
        this.stock = stock;
        this.likeCount = likeCount;
        this.status = status;
        this.productName = productName;
        this.price = price;
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public static ProductSummaryView of(Product product, Brand brand) {
        return ProductSummaryView.builder()
                .stock(product.getStock())
                .likeCount(product.getLikeCount())
                .status(product.getStatus())
                .productName(product.getName())
                .price(product.getPrice())
                .brandId(brand.getBrandId())
                .brandName(brand.getName())
                .build();
    }
}
