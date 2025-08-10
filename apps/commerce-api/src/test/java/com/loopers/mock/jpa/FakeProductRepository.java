package com.loopers.mock.jpa;

import com.loopers.application.product.dto.ProductSortType;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeProductRepository extends FakeJpaRepository<Product> implements ProductRepository {

    @Override
    protected Long getId(Product product) {
        return product.getId();
    }

    @Override
    public long getTotalCount() {
        return data.size();
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return null;
    }

    @Override
    public List<Product> findAllByIdsWithPessimisticLock(List<Long> productIds) {
        return null;
    }

    @Override
    public boolean incrementLikeCount(Long productId) {
        return true;
    }

    @Override
    public boolean decrementLikeCount(Long productId) {
        return true;
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return data.stream()
                .filter(p -> p.getId() != null && p.getId().equals(productId))
                .findFirst();
    }

    @Override
    public List<Product> findProducts(int page, int size, ProductSortType productSortType) {
        Comparator<Product> comparator = switch (productSortType) {
            case LATEST -> Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case PRICE_ASC -> Comparator.comparing(product -> product.getPrice().getValue());
            case LIKES_DESC -> Comparator.comparing(Product::getLikeCount, Comparator.reverseOrder());
        };

        List<Product> sortedList = data.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        int start = (page - 1) * size;
        int end = Math.min(start + size, sortedList.size());

        if (start >= sortedList.size()) {
            return Collections.emptyList();
        }

        return sortedList.subList(start, end);
    }
}
