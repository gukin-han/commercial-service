package com.loopers.interfaces.api.user;

import com.loopers.application.user.dto.SignUpCommand;
import com.loopers.application.user.dto.SignUpResult;
import com.loopers.domain.user.User;
import com.loopers.support.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class UserV1Dto {

    @Data
    @Builder
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class UserResponse {

        private String email;
        private String loginId;
        private String dateOfBirth;

        public static UserResponse fromEntity(User user) {
            return UserResponse.builder()
                    .email(user.getEmail())
                    .loginId(user.getLoginId())
                    .dateOfBirth(user.getDateOfBirth())
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
