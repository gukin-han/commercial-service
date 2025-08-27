package com.loopers.application.order;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final ProductService productService;
    private final OrderService orderService;
    private final CouponService couponService;

    @Transactional
    public OrderResult.Create create(OrderCommand.Create command) {
        Map<Long, Stock> productIdToStockMap = this.getProductIdStockMap(command.items());

        // 1. 상품 재고 차감
        productService.deductStocks(productIdToStockMap);

        // 2. 쿠폰 적용 및 할인 계산
        Money totalPrice = productService.calculateTotalPrice(productIdToStockMap);
        Money discountPrice = couponService.apply(command.couponId(), command.userId(), totalPrice);

        // 3. 주문 생성
        Order order = orderService.create(command.userId(), productIdToStockMap, totalPrice, discountPrice);

        return OrderResult.Create.success(order.getId());
    }


    private Map<Long, Stock> getProductIdStockMap(List<OrderCommand.CartItem> items) {
        return items.stream()
                .collect(Collectors.toMap(
                        OrderCommand.CartItem::productId,
                        it -> Stock.of(it.quantity()))
                );
    }
}
