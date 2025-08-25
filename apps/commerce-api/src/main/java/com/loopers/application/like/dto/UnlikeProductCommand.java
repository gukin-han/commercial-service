package com.loopers.application.like.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UnlikeProductCommand {

    private Long userId;
    private Long productId;

    @Builder
    private UnlikeProductCommand(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static UnlikeProductCommand of(Long userId, Long productId) {
        return UnlikeProductCommand.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }
}
