package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.user.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class ProductLike extends BaseEntity {

    @Embedded
    private UserId userId;
    @Embedded
    private ProductId productId;

    @Builder
    private ProductLike(UserId userId, ProductId productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public static ProductLike create(UserId userId, ProductId productId) {
        return ProductLike.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }

    public ProductLikeId getProductLikeId() {
        return getId() == null ? null : ProductLikeId.of(getId());
    }
}
