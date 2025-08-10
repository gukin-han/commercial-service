package com.loopers.application.product.dto;

import com.loopers.interfaces.api.product.ProductV1Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductPageQuery {

    private final Long brandId;
    private final int page;
    private final int size;
    private final ProductSortType sortType;

    public static ProductPageQuery create(int page, int size, ProductSortType sortType) {
        return ProductPageQuery.builder()
                .page(page)
                .size(size)
                .sortType(sortType)
                .build();
    }

    public static ProductPageQuery from(ProductV1Dto.GetProductsRequest request) {
        return ProductPageQuery.builder()
                .brandId(request.getBrandId())
                .page(request.getPage())
                .size(request.getSize())
                .sortType(request.getSortType())
                .build();
    }
}
