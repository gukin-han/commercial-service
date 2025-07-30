package com.loopers.application.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductAppService {

    private final ProductRepository productRepository;

    public Product findByProductId(ProductId productId) {
        return productRepository.getById(productId.value())
                .orElseThrow(EntityNotFoundException::new);
    }
}
