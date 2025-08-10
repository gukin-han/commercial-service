package com.loopers.application.product;

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
        Product product = productService.findByProductId(ProductId.of(query.getProductId()));
        Brand brand = brandService.findByBrandId(product.getBrandId());

        return ProductDetailView.create(product, brand);
    }

    @Transactional(readOnly = true)
    public PagedResult<ProductSummaryView> getPagedProducts(ProductPageQuery query) {
        BrandId brandId = query.getBrandId() == null ? null : BrandId.of(query.getBrandId());
        List<Product> products = productService.findProducts(brandId, query.getPage(), query.getSize(), query.getSortType());
        Map<BrandId, Brand> brandMap = getBrandIdToBrandMapFrom(products);
        List<ProductSummaryView> views = this.createProductSummaryViewsFrom(products, brandMap);

        return PagedResult.of(views, query.getPage(), productRepository.getTotalCountByBrandId(brandId), query.getSize());
    }

    private Map<BrandId, Brand> getBrandIdToBrandMapFrom(List<Product> products) {
        List<BrandId> brandIds = products.stream()
                .map(Product::getBrandId)
                .distinct()
                .toList();

        return brandService.findAllByIds(brandIds).stream()
                .collect(Collectors.toMap(Brand::getBrandId, b -> b));
    }

    private List<ProductSummaryView> createProductSummaryViewsFrom(List<Product> products, Map<BrandId, Brand> brandMap) {
        return products.stream()
                .map(p -> {
                    Brand b = brandMap.get(p.getBrandId());
                    return ProductSummaryView.of(p, b);
                })
                .toList();
    }
}
