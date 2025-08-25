package com.loopers.application.like.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UnlikeCommand {

    private String loginId;
    private Long productId;

    @Builder
    private UnlikeCommand(String loginId, Long productId) {
        this.loginId = loginId;
        this.productId = productId;
    }

    public static UnlikeCommand of(String loginId, Long productId) {
        return UnlikeCommand.builder()
                .loginId(loginId)
                .productId(productId)
                .build();
    }
}
