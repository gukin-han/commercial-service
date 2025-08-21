package com.loopers.infrastructure.data.order;

import com.loopers.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long>  {
}
