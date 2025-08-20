package com.loopers.application.order;

import com.loopers.application.payment.PaymentDispatcher;
import com.loopers.domain.coupon.CouponService;
import com.loopers.application.order.dto.Cart;
import com.loopers.application.order.dto.PlaceOrderCommand;
import com.loopers.application.order.dto.PlaceOrderResult;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.product.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final PlatformTransactionManager tm;

    private final ProductService productService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final PaymentDispatcher paymentDispatcher;

    public PlaceOrderResult placeOrder(PlaceOrderCommand command) {
        Map<ProductId, Stock> productIdToStockMap = this.getProductIdStockMap(command.getCart());

        TransactionTemplate tx = new TransactionTemplate(tm);
        Order pending = tx.execute(status -> {
            // 1. 상품 재고 차감
            productService.deductStocks(productIdToStockMap);

            // 2. 쿠폰 적용 및 할인 계산
            Money totalPrice = productService.calculateTotalPrice(productIdToStockMap);
            Money discountPrice = couponService.apply(command.getCouponId(), command.getUserId(), totalPrice);

            // 3. 주문 생성
            return orderService.create(command.getUserId(), productIdToStockMap, totalPrice, discountPrice);
        });

        // 4. 결제 요청
        PayResult payResult = paymentDispatcher.requestPayment(PayCommand.from(pending, command.getPaymentMethod()));

        return PlaceOrderResult.success(pending.getOrderId());
    }


    private Map<ProductId, Stock> getProductIdStockMap(Cart cart) {
        return cart.getCartItems().stream()
                .collect(Collectors.toMap(
                        it -> ProductId.of(it.getProductId()),
                        it -> Stock.of(it.getQuantity()))
                );
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class Pending {
        private Long orderId;
        private Long userId;
        private Money payAmount;
    }
}
