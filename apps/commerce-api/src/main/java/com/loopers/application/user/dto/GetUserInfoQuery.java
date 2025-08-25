package com.loopers.application.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetUserInfoQuery {

    private final String loginId;

    @Builder
    public GetUserInfoQuery(String loginId) {
        this.loginId = loginId;
    }
}
