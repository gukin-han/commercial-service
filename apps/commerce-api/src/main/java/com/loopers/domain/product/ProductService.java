package com.loopers.domain.product;

import com.loopers.application.product.dto.ProductSortType;
import com.loopers.domain.brand.BrandId;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product findByProductId(ProductId productId) {
        return productRepository.findById(productId.getValue())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "조회할 수 없는 상품입니다."));
    }

    @Transactional(readOnly = true)
    public List<ProductDetail> findProducts(BrandId brandId, int page, int size, ProductSortType sortType) {
        return productRepository.findPagedProductDetails(brandId, page, size, sortType);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public List<Product> getAllByIdsIn(List<ProductId> productIds) {
        List<Long> sortedProductIds = productIds.stream()
                .map(ProductId::getValue)
                .sorted()
                .toList();

        List<Product> products = productRepository.findAllByIdsWithPessimisticLock(sortedProductIds);

        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("조회결과, 존재하지 않는 상품이 있습니다.");
        }
        return products;
    }

    @Transactional
    public List<Product> deductStocks(Map<ProductId, Stock> productIdToStockMap) {
        List<Long> sortedProductIds = productIdToStockMap.keySet().stream()
                .map(ProductId::getValue)
                .sorted()
                .toList();

        List<Product> products = productRepository.findAllByIdsWithPessimisticLock(sortedProductIds);

        if (products.size() != sortedProductIds.size()) {
            throw new IllegalArgumentException("조회결과, 존재하지 않는 상품이 있습니다.");
        }
        for (Product product : products) {
            ProductId productId = product.getProductId();
            Stock stock = productIdToStockMap.get(productId);
            product.decreaseStock(stock.getQuantity());
        }

        return products;
    }

    @Transactional
    public Money calculateTotalPrice(List<Product> products, Map<ProductId, Stock> productIdToStockMap) {
        Money totalPrice = Money.of(0L);
        for (Product product : products) {
            Money unitPrice = product.getPrice();
            Stock stock = productIdToStockMap.get(product.getProductId());
            totalPrice = totalPrice.add(unitPrice.multiply(stock.getQuantity()));
        }
        return totalPrice;
    }
}
