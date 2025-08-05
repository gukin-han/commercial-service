package com.loopers.domain.order;

import com.loopers.domain.product.Money;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.Stock;
import com.loopers.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order create(User user, Map<ProductId, Stock> productIdToStockMap, Money totalPrice) {
        Order order = Order.of(user.getUserId(), totalPrice, OrderStatus.PAID);
        orderRepository.save(order);

        List<OrderItem> orderItems = productIdToStockMap.entrySet().stream()
                .map(entry -> OrderItem.of(order.getOrderId(), entry.getKey(), entry.getValue().getQuantity()))
                .toList();

        orderItemRepository.saveAll(orderItems);

        return order;
    }
}
