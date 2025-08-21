package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderId;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public void saveAll(List<OrderItem> orderItems) {
        orderItemJpaRepository.saveAll(orderItems);
    }

    @Override
    public List<OrderItem> findOrderItemsByOrderId(OrderId orderId) {
        return orderItemJpaRepository.findAllByOrderId(orderId);
    }
}
