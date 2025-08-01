package com.loopers.application.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.BrandService;
import com.loopers.application.common.dto.PagedResult;
import com.loopers.application.product.dto.ProductDetailQuery;
import com.loopers.application.product.dto.ProductDetailView;
import com.loopers.application.product.dto.ProductPageQuery;
import com.loopers.application.product.dto.ProductSummaryView;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandId;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductDetailView getProductDetail(ProductDetailQuery query) {
        Product product = productService.findByProductId(ProductId.of(query.productId()));
        Brand brand = brandService.findByBrandId(BrandId.of(query.brandId()));

        return ProductDetailView.create(product, brand);
    }

    @Transactional(readOnly = true)
    public PagedResult<ProductSummaryView> getPagedProducts(ProductPageQuery query) {
        List<Product> products = productRepository.findProducts(
                query.page(),
                query.size(),
                query.sortType()
        );

        Map<Long, Product> idToProductMap = products.stream()
                .collect(Collectors.toMap(BaseEntity::getId, p -> p));
        return null;
    }
}
