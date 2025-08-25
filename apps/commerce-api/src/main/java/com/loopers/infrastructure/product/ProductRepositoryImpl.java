package com.loopers.infrastructure.product;

import com.loopers.application.product.dto.ProductSortType;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.loopers.domain.product.QProduct.product;


@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    // SELECT
    @Override
    public Optional<Product> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public List<Product> findProducts(int page, int size, ProductSortType productSortType) {
        return jpaQueryFactory.selectFrom(product)
                .orderBy(productSort(productSortType))
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long getTotalCount() {
        return productJpaRepository.count();
    }

    @Override
    public List<Product> findAllByIdsWithPessimisticLock(List<Long> productIds) {
        return productJpaRepository.findAllForUpdate(productIds);
    }

    // INSERT
    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return productJpaRepository.saveAll(products);
    }

    // UPDATE
    @Override
    public boolean incrementLikeCount(Long productId) {
        return productJpaRepository.incrementLikeCount(productId) > 0;
    }

    @Override
    public boolean decrementLikeCount(Long productId) {
        return productJpaRepository.decrementLikeCount(productId) > 0;
    }

    private OrderSpecifier<?> productSort(ProductSortType productSortType) {
        return switch (productSortType) {
            case LATEST -> product.createdAt.desc();
            case PRICE_ASC -> product.price.value.asc();
            case LIKES_DESC -> product.likeCount.desc();
        };
    }
}
