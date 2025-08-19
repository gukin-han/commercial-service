package com.loopers.application.order;

import com.loopers.application.payment.PaymentDispatcher;
import com.loopers.domain.coupon.CouponService;
import com.loopers.application.order.dto.Cart;
import com.loopers.application.order.dto.PlaceOrderCommand;
import com.loopers.application.order.dto.PlaceOrderResult;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.product.*;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserId;
import com.loopers.domain.user.UserService;
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
    private final UserService userService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final PaymentDispatcher paymentDispatcher;

    @Transactional
    public PlaceOrderResult placeOrder(PlaceOrderCommand command) {
        // 1. 상품 재고 차감
        Map<ProductId, Stock> productIdToStockMap = this.getProductIdStockMap(command.getCart());
        List<Product> products = productService.deductStocks(productIdToStockMap);

        // 2. 쿠폰 적용 및 할인 계산
        Money totalPrice = productService.calculateTotalPrice(products, productIdToStockMap);
        Money discountPrice = couponService.apply(command.getCouponId(), command.getUserId(), totalPrice);

        // 3. 주문 생성
        Order order = orderService.createPending(command.getUserId(), productIdToStockMap, totalPrice, discountPrice);

        // 4. 결제 처리
        paymentDispatcher.pay(
                PayCommand.builder()
                        .userId(command.getUserId())
                        .amount(totalPrice.subtract(discountPrice))
                        .method(command.getPaymentMethod())
                        .build()
        );

        return PlaceOrderResult.success(order);
    }


    private Map<ProductId, Stock> getProductIdStockMap(Cart cart) {
        return cart.getCartItems().stream()
                .collect(Collectors.toMap(
                        it -> ProductId.of(it.getProductId()),
                        it -> Stock.of(it.getQuantity()))
                );
    }
}
