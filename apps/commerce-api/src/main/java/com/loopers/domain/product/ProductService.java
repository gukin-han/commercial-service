package com.loopers.domain.product;

import com.loopers.application.product.dto.ProductSortType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product findByProductId(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Product> findProducts(int page, int size, String keyword, ProductSortType productSortType) {
        return null;
    }
}
