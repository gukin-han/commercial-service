package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.domain.user.UserEntity;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.constant.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec{

    private final UserFacade userFacade;

    @Override
    @PostMapping
    public ApiResponse<UserV1Dto.UserResponse> signUp(@RequestBody UserV1Dto.RegisterRequest request) {
        UserEntity userEntity = userFacade.signUp(UserV1Dto.RegisterRequest.toEntity(request));
        return ApiResponse.success(UserV1Dto.UserResponse.fromEntity(userEntity));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserV1Dto.UserResponse> getMe(@RequestHeader("X-USER-ID") String userId) {
        UserEntity me = userFacade.getMe(userId);
        return ApiResponse.success(UserV1Dto.UserResponse.fromEntity(me)
        );
    }


}
