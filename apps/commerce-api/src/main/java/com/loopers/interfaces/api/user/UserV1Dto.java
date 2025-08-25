package com.loopers.interfaces.api.user;

import com.loopers.application.user.dto.SignUpCommand;
import com.loopers.application.user.dto.SignUpResult;
import com.loopers.application.user.dto.UserInfoView;
import com.loopers.support.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class UserV1Dto {

    @Data
    @Builder
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class GetUserInfoResponse {

        private String email;
        private String loginId;
        private String dateOfBirth;

        public static GetUserInfoResponse fromView(UserInfoView view) {
            return GetUserInfoResponse.builder()
                    .email(view.getEmail())
                    .loginId(view.getLoginId())
                    .dateOfBirth(view.getDateOfBirth())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class SignUpRequest {

        private String email;
        private String loginId;
        private String dateOfBirth;
        private Gender gender;

        public SignUpCommand toCommand() {
            return SignUpCommand.builder()
                    .email(this.email)
                    .loginId(this.loginId)
                    .dateOfBirth(this.dateOfBirth)
                    .gender(this.gender)
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class SignUpResponse {

        private String email;
        private String loginId;
        private String dateOfBirth;
        private Double balance;

        public static SignUpResponse fromResult(SignUpResult result) {
            return SignUpResponse.builder()
                    .email(result.getEmail())
                    .loginId(result.getLoginId())
                    .dateOfBirth(result.getDateOfBirth())
                    .balance(result.getBalance().getValue().doubleValue())
                    .build();
        }
    }
}