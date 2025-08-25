package com.loopers.application.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PagedResult<T> {

    private final List<T> items;
    private final int currentPage;
    private final int totalPages;
    private final long totalItems;
    private final boolean hasNext;

    @Builder
    private PagedResult(List<T> items, int currentPage, int totalPages, long totalItems, boolean hasNext) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.hasNext = hasNext;
    }

    // 정적 팩토리 메서드: 페이지네이션 계산 로직을 캡슐화
    public static <T> PagedResult<T> of(List<T> items, int currentPage, long totalItems, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        boolean hasNext = currentPage < totalPages - 1;

        return PagedResult.<T>builder()
                .items(items)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .hasNext(hasNext)
                .build();
    }
}
