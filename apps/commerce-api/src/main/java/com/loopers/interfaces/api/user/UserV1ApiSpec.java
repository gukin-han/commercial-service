package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;

public interface UserV1ApiSpec {

    ApiResponse<UserV1Dto.UserResponse> signUp(
            UserV1Dto.RegisterRequest request
    );

    ApiResponse<UserV1Dto.UserResponse> getMe(String UserId);

}
