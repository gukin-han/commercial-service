package com.loopers.mock.jpa;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;

import java.util.Objects;
import java.util.Optional;

public class FakeProductLikeRepository extends FakeJpaRepository<ProductLike> implements ProductLikeRepository {

    @Override
    public Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId) {
        return data.stream()
                .filter(like -> Objects.equals(like.getUserId().getValue(), userId)
                        && Objects.equals(like.getProductId().getValue(), productId))
                .findFirst();
    }

    @Override
    public void delete(ProductLike productLike) {
        data.removeIf(like -> like.getId().equals(productLike.getId()));
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return data.stream()
                .anyMatch(like -> Objects.equals(like.getUserId().getValue(), userId)
                        && Objects.equals(like.getProductId().getValue(), productId));
    }

    @Override
    protected Long getId(ProductLike entity) {
        return entity.getId();
    }

    @Override
    public ProductLike save(ProductLike productLike) {
        if (existsByUserIdAndProductId(productLike.getUserId().getValue(), productLike.getProductId().getValue())) {
            return productLike;
        }
        return super.save(productLike);
    }
}
