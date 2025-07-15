package com.loopers.interfaces.api.user;

import com.loopers.domain.user.UserEntity;
import com.loopers.support.constant.Gender;

public class UserV1Dto {

    public record UserResponse(String email, String userId, String dateOfBirth) {
        public static UserResponse fromEntity(UserEntity userEntity) {
            return new UserResponse(userEntity.getEmail(), userEntity.getUserId(), userEntity.getDateOfBirth());
        }
    }

    public record RegisterRequest(String email, String userId, String birthday, Gender gender) {
        public static UserEntity toEntity(RegisterRequest request) {
            return UserEntity.builder()
                    .email(request.email)
                    .userId(request.userId)
                    .dateOfBirth(request.birthday)
                    .gender(request.gender)
                    .build();
        }
    }
}
