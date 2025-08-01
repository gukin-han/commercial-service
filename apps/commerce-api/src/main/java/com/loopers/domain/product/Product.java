package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.BrandId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
@Entity
public class Product extends BaseEntity {

    @Embedded
    private Stock stock;

    @Column(nullable=false)
    private long likeCount;

    @Enumerated(value = EnumType.STRING)
    private ProductStatus status;

    private String name;

    @Embedded
    private Money price;

    @Embedded
    private BrandId brandId;

    @Builder
    private Product(Stock stock, long likeCount, ProductStatus status, String name, Money price, BrandId brandId) {
        this.stock = stock;
        this.likeCount = likeCount;
        this.status = status;
        this.name = name;
        this.price = price;
        this.brandId = brandId;
    }

    public Product(long stockQuantity) {

        this.stock = Stock.of(stockQuantity);
        this.likeCount = 0;
        this.status = ProductStatus.ACTIVE;
    }

    public void decreaseStock(long quantity) {

        this.canDecrease();
        Stock decreasedStock = stock.decrease(quantity);
        if (decreasedStock.isSoldOut()) {
            this.status = ProductStatus.SOLD_OUT;
        }
        this.stock = decreasedStock;
    }

    private void canDecrease() {
        if (this.status == ProductStatus.SOLD_OUT) {
            throw new CoreException(ErrorType.CONFLICT, "재고 수량이 없습니다.");
        }

        if (this.status == ProductStatus.STOPPED) {
            throw new CoreException(ErrorType.CONFLICT, "판매가 중단된 상품입니다.");
        }
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount - 1 < 0) {
            throw new CoreException(ErrorType.CONFLICT, "상품의 좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount--;
    }

    public ProductId getProductId() {
        return getId() == null ? null : ProductId.of(getId());
    }
}
