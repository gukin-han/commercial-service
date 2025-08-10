package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "유저 관리 API")
public interface UserV1ApiSpec {

    @Operation(
            summary = "회원가입",
            description = "유저의 새로운 계정을 생성한다."
    )
    ApiResponse<UserV1Dto.SignUpResponse> signUp(
            UserV1Dto.SignUpRequest request
    );

    @Operation(
            summary = "내 정보 조회",
            description = "로그인 유저의 정보를 조회한다."
    )
    ApiResponse<UserV1Dto.GetUserInfoResponse> getUserInfo(
            @Schema(name = "X-USER-ID", description = "로그인 유저의 ID")
            String loginId
    );
}
