package com.loopers.domain.product;

import com.loopers.application.product.dto.ProductSortType;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long productId);

    List<Product> findAllByIds(List<Long> productIds);

    List<Product> findProducts(int page, int size, ProductSortType productSortType);

    Product save(Product product);

    long getTotalCount();

    List<Product> saveAll(List<Product> products);

    List<Product> findAllByIdsWithPessimisticLock(List<Long> productIds);
}
