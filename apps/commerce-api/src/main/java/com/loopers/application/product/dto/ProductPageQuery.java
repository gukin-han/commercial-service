package com.loopers.application.product.dto;

import com.loopers.interfaces.api.product.ProductV1Dto;
import lombok.Builder;
import lombok.Data;

@Data
public class ProductPageQuery {

    private final int page;
    private final int size;
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

    public static ProductPageQuery from(ProductV1Dto.GetProductsRequest request) {
        return ProductPageQuery.builder()
                .page(request.getPage())
                .size(request.getSize())
                .sortType(request.getSortType())
                .build();
    }
}
