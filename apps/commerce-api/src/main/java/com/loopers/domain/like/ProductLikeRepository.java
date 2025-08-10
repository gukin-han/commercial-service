package com.loopers.domain.like;

import com.loopers.domain.product.ProductId;
import com.loopers.domain.user.UserId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductLikeRepository {
    ProductLike save(ProductLike productLike);
    Optional<ProductLike> findByUserIdAndProductId(UserId userId, ProductId productId);

    boolean insertIgnoreDuplicateKey(UserId userId, ProductId productId);

    boolean deleteByProductIdAndUserId(UserId userId, ProductId productId);
}
