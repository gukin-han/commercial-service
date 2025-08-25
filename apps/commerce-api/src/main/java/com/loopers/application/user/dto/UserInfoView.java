package com.loopers.application.user.dto;

import com.loopers.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoView {

    private final String email;
    private final String loginId;
    private final String dateOfBirth;

    @Builder
    public UserInfoView(String email, String loginId, String dateOfBirth) {
        this.email = email;
        this.loginId = loginId;
        this.dateOfBirth = dateOfBirth;
    }

    public static UserInfoView from(User user) {
        return UserInfoView.builder()
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}
