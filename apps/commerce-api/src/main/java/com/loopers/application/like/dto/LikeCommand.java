package com.loopers.application.like.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LikeCommand {

    private String loginId;
    private Long productId;

    @Builder
    private LikeCommand(String loginId, Long productId) {
        this.loginId = loginId;
        this.productId = productId;
    }

    public static LikeCommand of(String loginId, Long productId) {
        return LikeCommand.builder()
                .loginId(loginId)
                .productId(productId)
                .build();
    }
}
