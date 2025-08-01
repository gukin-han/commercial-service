package com.loopers.application.like.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LikeProductCommand {

    private Long userId;
    private Long productId;

    @Builder
    private LikeProductCommand(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static LikeProductCommand of(Long userId, Long productId) {
        return LikeProductCommand.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }
}
