package com.loopers.application.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductService;
import com.loopers.mock.FakeProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void init() {
        ProductRepository productRepository = new FakeProductRepository();
        productService = new ProductService(productRepository);
    }

    @DisplayName("상품 조회시")
    @Nested
    class findByProductId {


        @DisplayName("존재하지 않는 상품 ID로 상품 조회하는 경우 EntityNotFound 예외를 던진다")
        @Test
        void throwsEntityNotFoundException_whenProductIdIsNotFound() {
            Assertions.assertThatThrownBy(() -> productService.findByProductId(ProductId.of(999L)))
                    .isInstanceOf(EntityNotFoundException.class);
        }

    }


}
