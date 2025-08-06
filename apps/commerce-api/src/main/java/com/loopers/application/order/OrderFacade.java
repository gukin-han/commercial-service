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
        Map<ProductId, Stock> productIdToStockMap = this.getProductIdStockMap(command.getCart());

        // 1. 사용자 정보 조회
        User user = userService.findByUserId(UserId.of(command.getUserId()));

        // 2. 상품 재고 차감
        List<Product> products = productService.getAllByIdsIn(productIdToStockMap.keySet().stream().toList());
        productService.deductStocks(products, productIdToStockMap);

        // 3. 쿠폰 적용 및 할인 계산
        Money totalPrice = productService.calculateTotalPrice(products, productIdToStockMap);
        Money discountAmount = Money.ZERO;
        Coupon coupon = null;
        if (command.getCouponId() != null) {
            coupon = couponQueryService.getCouponByCouponIdAndUserId(CouponId.of(command.getCouponId()), user.getUserId());
            CouponDiscountCalculator calculator = new CouponDiscountCalculator();
            discountAmount = calculator.calculateDiscountAmount(coupon, totalPrice);
        }

        // 4. 주문 생성
        Order order = orderService.create(user, productIdToStockMap, totalPrice, discountAmount);

        // 5. 포인트 차감
        Point point = pointService.findByUserId(user.getUserId());
        pointService.deductPoint(point, totalPrice.subtract(discountAmount));

        // 6. 쿠폰 사용처리
        if (coupon != null) {
            coupon.use(order);
            couponRepository.save(coupon);
        }

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
