package com.loopers.interfaces.api.user;

import com.loopers.domain.user.User;
import com.loopers.support.constant.Gender;

public class UserV1Dto {

    public record UserResponse(String email, String userId, String dateOfBirth) {

        public static UserResponse fromEntity(User user) {
            return new UserResponse(user.getEmail(), user.getLoginId(), user.getDateOfBirth());
        }
    }

    public record RegisterRequest(String email, String loginId, String birthday, Gender gender) {

        public static User toEntity(RegisterRequest request) {
            return User.builder()
                    .email(request.email)
                    .loginId(request.loginId)
                    .dateOfBirth(request.birthday)
                    .gender(request.gender)
                    .build();
        }
    }
}
