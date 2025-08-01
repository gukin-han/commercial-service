package com.loopers.application.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ProductPageQuery {

    private final int page;
    private int size;
    private final ProductSortType sortType;

    @Builder
    private ProductPageQuery(int page, int size, ProductSortType sortType) {
        this.page = page;
        this.size = size;
        this.sortType = sortType;
    }

    public static ProductPageQuery create(int page, int size, ProductSortType sortType) {
        return ProductPageQuery.builder()
                .page(page)
                .size(size)
                .sortType(sortType)
                .build();
    }
}
