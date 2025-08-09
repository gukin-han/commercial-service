package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.dto.SignUpResult;
import com.loopers.domain.user.User;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.loopers.interfaces.api.ApiHeader.LOGIN_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @Override
    @PostMapping
    public ApiResponse<UserV1Dto.SignUpResponse> signUp(
            @RequestBody UserV1Dto.SignUpRequest request
    ) {
        SignUpResult result = userFacade.signUp(request.toCommand());

        return ApiResponse.success(UserV1Dto.SignUpResponse.fromResult(result));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserV1Dto.UserResponse> getUserInfo(
            @RequestHeader(LOGIN_ID) String loginId
    ) {
        User me = userFacade.getUserInfo(loginId);

        return ApiResponse.success(UserV1Dto.UserResponse.fromEntity(me));
    }
}
