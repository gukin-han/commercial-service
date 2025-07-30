package com.loopers.application.product;

import com.loopers.application.brand.BrandAppService;
import com.loopers.application.product.dto.ProductDetailQuery;
import com.loopers.application.product.dto.ProductDetailView;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandId;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductAppService productAppService;
    private final BrandAppService brandAppService;

    @Transactional
    public ProductDetailView getDetailView(ProductDetailQuery query) {
        Product product = productAppService.findByProductId(ProductId.of(query.productId()));
        Brand brand = brandAppService.findByBrandId(BrandId.of(query.brandId()));

        return ProductDetailView.create(product, brand);
    }
}
