package com.loopers.domain.product;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product findByProductId(ProductId productId) {
        return productRepository.findById(productId.getValue()).orElseThrow(EntityNotFoundException::new);
    }

    public List<Product> findAllByIds(List<ProductId> productIds) {
        return productRepository.findAllByIds(productIds.stream().map(ProductId::getValue).collect(Collectors.toList()));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllByIdsIn(List<ProductId> productIds) {
        List<Product> products = productRepository.findAllByIds(productIds.stream()
                .map(ProductId::getValue)
                .toList()
        );

        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("조회결과, 존재하지 않는 상품이 있습니다.");
        }
        return products;
    }

    public void deductStocks(List<Product> products, Map<ProductId, Stock> productIdToStockMap) {
        for (Product product : products) {
            ProductId productId = product.getProductId();
            Stock stock = productIdToStockMap.get(productId);
            this.deductStock(product, stock);
        }
    }

    private void deductStock(Product product, Stock stock) {
        product.decreaseStock(stock.getQuantity());
    }

    public long calculateTotalPrice(List<Product> products, Map<ProductId, Stock> productIdToStockMap) {
        long totalPrice = 0;
        for (Product product : products) {
            Money unitPrice = product.getPrice();
            Stock stock = productIdToStockMap.get(product);
            totalPrice += stock.getQuantity() + unitPrice.getValue();
        }
        return totalPrice;
    }
}
