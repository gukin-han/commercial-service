package com.loopers.infrastructure.data.like;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductLikeRepositoryImpl implements ProductLikeRepository {

    private final ProductLikeJpaRepository productLikeJpaRepository;

    @Override
    public ProductLike save(ProductLike productLike) {
        return productLikeJpaRepository.save(productLike);
    }

    @Override
    public Optional<ProductLike> findByUserIdAndProductId(UserId userId, ProductId productId) {
        return productLikeJpaRepository.findByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean insertIgnoreDuplicateKey(UserId userId, ProductId productId) {
        return productLikeJpaRepository.insertIgnoreDuplicateKey(userId.getValue(), productId.getValue()) > 0;
    }

    @Override
    public boolean deleteByProductIdAndUserId(UserId userId, ProductId productId) {
        return productLikeJpaRepository.deleteByProductIdAndUserId(productId, userId) > 0;
    }

}
