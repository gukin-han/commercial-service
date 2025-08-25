package com.loopers.domain.product;

import com.loopers.domain.brand.BrandId;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductDetail {
    private Long productId;
    private String productName;
    private Money price;
    private Stock stock;
    private Long likeCount;
    private BrandId brandId;
    private String brandName;

    public ProductDetail(Long productId, String productName, Money price, Stock stock, Long likeCount, Long brandId, String brandName) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.likeCount = likeCount;
        this.brandId = BrandId.of(brandId);
        this.brandName = brandName;
    }
}
