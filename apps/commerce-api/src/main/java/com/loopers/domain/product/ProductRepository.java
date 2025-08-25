package com.loopers.domain.product;

import com.loopers.application.product.dto.ProductSortType;
import com.loopers.domain.brand.BrandId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long productId);

    List<ProductDetail> findPagedProductDetails(BrandId brandId, int page, int size, ProductSortType productSortType);

    Product save(Product product);

    long getTotalCountByBrandId(BrandId brandId);

    List<Product> saveAll(List<Product> products);

    List<Product> findAllByIdsWithPessimisticLock(List<Long> productIds);

    boolean incrementLikeCount(Long productId);

    boolean decrementLikeCount(Long productId);

    List<Product> findAllById(List<Long> productIds);
}
