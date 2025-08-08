package com.loopers.interfaces.api.user;

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
        private String birthday;
        private Gender gender;

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .loginId(this.loginId)
                    .dateOfBirth(this.birthday)
                    .gender(this.gender)
                    .build();
        }
    }
}
