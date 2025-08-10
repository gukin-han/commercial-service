package com.loopers.domain.product;

import com.loopers.application.product.dto.ProductSortType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long productId);

    List<Product> findProducts(int page, int size, ProductSortType productSortType);

    Product save(Product product);

    long getTotalCount();

    List<Product> saveAll(List<Product> products);

    List<Product> findAllByIdsWithPessimisticLock(List<Long> productIds);

    boolean incrementLikeCount(Long productId);

    boolean decrementLikeCount(Long productId);
}
