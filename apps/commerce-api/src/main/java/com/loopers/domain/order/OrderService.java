package com.loopers.domain.order;

import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.Stock;
import com.loopers.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order create(Long userId, Map<ProductId, Stock> productIdToStockMap, Money totalPrice, Money discountAmount) {
        Order order = orderRepository.save(Order.of(UserId.of(userId), totalPrice, discountAmount, OrderStatus.CREATED));

        List<OrderItem> orderItems = productIdToStockMap.entrySet().stream()
                .map(entry -> OrderItem.of(order.getOrderId(), entry.getKey(), entry.getValue().getQuantity()))
                .toList();

        orderItemRepository.saveAll(orderItems);
        return order;
    }
}
