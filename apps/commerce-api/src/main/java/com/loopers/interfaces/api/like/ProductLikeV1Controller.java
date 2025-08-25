package com.loopers.interfaces.api.like;

import com.loopers.application.like.ProductLikeFacade;
import com.loopers.application.like.dto.LikeCommand;
import com.loopers.application.like.dto.LikeProductResult;
import com.loopers.application.like.dto.UnlikeCommand;
import com.loopers.application.like.dto.UnlikeProductResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.loopers.interfaces.api.ApiHeader.LOGIN_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/likes")
public class ProductLikeV1Controller implements ProductLikeV1ApiSpec{

    private final ProductLikeFacade productLikeFacade;

    @Override
    @PostMapping("/{productId}")
    public ApiResponse<ProductLikeV1Dto.LikeResponse> like(
            @RequestHeader(LOGIN_ID) String loginId,
            @PathVariable(value = "productId") Long productId
    ) {
        LikeProductResult result = productLikeFacade.like(LikeCommand.of(loginId, productId));
        return ApiResponse.success(ProductLikeV1Dto.LikeResponse.fromResult(result));
    }

    @Override
    @DeleteMapping("/{productId}")
    public ApiResponse<ProductLikeV1Dto.UnlikeResponse> unlike(
            @RequestHeader(LOGIN_ID) String loginId,
            @PathVariable(value = "productId") Long productId
    ) {
        UnlikeProductResult result = productLikeFacade.unlike(UnlikeCommand.of(loginId, productId));
        return ApiResponse.success(ProductLikeV1Dto.UnlikeResponse.fromResult(result));
    }
}
