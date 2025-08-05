package com.loopers.application.order;

import com.loopers.application.coupon.CouponQueryService;
import com.loopers.application.order.dto.Cart;
import com.loopers.application.order.dto.PlaceOrderCommand;
import com.loopers.application.order.dto.PlaceOrderResult;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponDiscountCalculator;
import com.loopers.domain.coupon.CouponId;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
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
    private final PointService pointService;
    private final OrderService orderService;
    private final CouponQueryService couponQueryService;
    private final CouponRepository couponRepository;

    @Transactional
    public PlaceOrderResult placeOrder(PlaceOrderCommand command) {
        Cart cart = command.getCart();
        Map<ProductId, Stock> productIdToStockMap = this.getProductIdStockMap(cart);

        // 1. 사용자 정보 조회
        User user = userService.findByUserId(UserId.of(command.getUserId()));

        // 2. 상품 조회
        List<Product> products = productService.getAllByIdsIn(productIdToStockMap.keySet().stream().toList());

        // 3. 상품 재고 차감
        productService.deductStocks(products, productIdToStockMap);
        Money totalPrice = productService.calculateTotalPrice(products, productIdToStockMap);

        // 4. 쿠폰 조회
        Coupon coupon = couponQueryService.getCouponByCouponIdAndUserId(CouponId.of(command.getCouponId()), user.getUserId());
        CouponDiscountCalculator calculator = new CouponDiscountCalculator();
        Money discountAmount = calculator.calculateDiscountAmount(coupon, totalPrice);

        // 5. 포인트 차감
        Point point = pointService.findByUserId(user.getUserId());
        pointService.deductPoint(point, totalPrice.subtract(discountAmount));

        // 6. 주문 생성
        Order order = orderService.create(user, productIdToStockMap, totalPrice, discountAmount);
        coupon.use(order);
        couponRepository.save(coupon);
        return PlaceOrderResult.SUCCESS(order);
    }


    private Map<ProductId, Stock> getProductIdStockMap(Cart cart) {
        return cart.getCartItems().stream()
                .collect(Collectors.toMap(
                        it -> ProductId.of(it.getProductId()),
                        it -> Stock.of(it.getQuantity()))
                );
    }
}
