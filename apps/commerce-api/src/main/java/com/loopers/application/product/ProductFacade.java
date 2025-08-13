package com.loopers.application.product;

import com.loopers.domain.brand.BrandService;
import com.loopers.application.common.dto.PagedResult;
import com.loopers.application.product.dto.ProductDetailQuery;
import com.loopers.application.product.dto.ProductDetailView;
import com.loopers.application.product.dto.ProductPageQuery;
import com.loopers.application.product.dto.ProductSummaryView;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandId;
import com.loopers.domain.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductDetailView getProductDetail(ProductDetailQuery query) {
        Product product = productService.findByProductId(ProductId.of(query.getProductId()));
        Brand brand = brandService.findByBrandId(product.getBrandId());

        return ProductDetailView.create(product, brand);
    }

    @Transactional(readOnly = true)
    public PagedResult<ProductSummaryView> getPagedProducts(ProductPageQuery query) {
        BrandId brandId = query.getBrandId() == null ? null : BrandId.of(query.getBrandId());
        List<ProductSummaryView> views = productService.findProducts(brandId, query.getPage(), query.getSize(), query.getSortType()).stream()
                .map(ProductSummaryView::from)
                .collect(Collectors.toList());

        return PagedResult.of(views, query.getPage(), productRepository.getTotalCountByBrandId(brandId), query.getSize());
    }
}
