package com.loopers.interfaces.api.product;

import com.loopers.application.common.dto.PagedResult;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.dto.ProductDetailQuery;
import com.loopers.application.product.dto.ProductDetailView;
import com.loopers.application.product.dto.ProductPageQuery;
import com.loopers.application.product.dto.ProductSummaryView;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec{

    private final ProductFacade productFacade;

    @GetMapping
    @Override
    public ApiResponse<ProductV1Dto.GetProductsResponse> getProducts(
            ProductV1Dto.GetProductsRequest request
    ) {
        PagedResult<ProductSummaryView> pagedProducts = productFacade.getPagedProducts(ProductPageQuery.from(request));
        return ApiResponse.success(ProductV1Dto.GetProductsResponse.of(pagedProducts));
    }

    @GetMapping("/{productId}")
    @Override
    public ApiResponse<ProductV1Dto.GetProductByIdResponse> getProductById(
            ProductV1Dto.GetProductByIdRequest request
    ) {
        ProductDetailView view = productFacade.getProductDetail(ProductDetailQuery.from(request));
        return ApiResponse.success(ProductV1Dto.GetProductByIdResponse.of(view));
    }
}
