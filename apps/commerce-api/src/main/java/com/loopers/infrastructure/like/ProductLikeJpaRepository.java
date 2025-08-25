package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Optional<ProductLike> findByUserIdAndProductId(Long userId, Long productId);
}
