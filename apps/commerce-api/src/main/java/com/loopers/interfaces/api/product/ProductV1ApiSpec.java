package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "Product V1 API", description = "상품 관리 API")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회한다."
    )
    ApiResponse<ProductV1Dto.GetProductsResponse> getProducts(
            @ParameterObject
            ProductV1Dto.GetProductsRequest request
    );

    @Operation(
            summary = "상품 정보 조회",
            description = "상품 정보를 조회한다."
    )
    ApiResponse<ProductV1Dto.GetProductByIdResponse> getProductById(
            @ParameterObject
            ProductV1Dto.GetProductByIdRequest request
    );

}
