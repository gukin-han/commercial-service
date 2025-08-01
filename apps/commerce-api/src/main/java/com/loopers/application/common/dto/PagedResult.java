package com.loopers.application.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public final class PagedResult<T> {

    private final List<T> items;
    private final int currentPage;
    private final int totalPages;
    private final long totalItems;
    private final boolean hasNext;
}
