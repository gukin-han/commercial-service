package com.loopers.application.product.dto;

public record ProductPageQuery(int page, int size, String keyword, ProductSortType sortType) {
}
