package com.loopers.application.order;

import com.loopers.application.order.dto.Cart;
import com.loopers.application.order.dto.CartItem;
import com.loopers.application.order.dto.PlaceOrderCommand;
import com.loopers.application.order.dto.PlaceOrderResult;
import com.loopers.domain.brand.BrandId;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.Percent;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderItemRepository;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.Stock;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.constant.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderFacadeIntegrationTest {

    private final DatabaseCleanUp databaseCleanUp;
    private final OrderFacade orderFacade;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PointRepository pointRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    OrderFacadeIntegrationTest(DatabaseCleanUp databaseCleanUp, OrderFacade orderFacade, UserRepository userRepository, ProductRepository productRepository, PointRepository pointRepository, CouponRepository couponRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.databaseCleanUp = databaseCleanUp;
        this.orderFacade = orderFacade;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.pointRepository = pointRepository;
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("주문 요청시")
    @Nested
    class PlaceOrder {

        @DisplayName("정상 처리된다")
        @Test
        void shouldPlaceOrderSuccessfully() {
            //given
            User user = User.create("gukin", "gukin@email.com", "2025-10-10", Gender.FEMALE);
            User savedUser = userRepository.save(user);

            Point point = Point.create(savedUser, Money.of(1_000_000));
            Point savedPoint = pointRepository.save(point);

            Coupon coupon = Coupon.create(CouponType.PERCENTAGE, savedUser.getUserId(), null, Percent.of(0.2));
            Coupon savedCoupon = couponRepository.save(coupon);

            Product product1 = Product.create(Stock.of(5), "맥북", Money.of(10_000), BrandId.of(1L));
            Product product2 = Product.create(Stock.of(5), "아이폰", Money.of(5_000), BrandId.of(1L));
            List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

            List<CartItem> items = new ArrayList<>();
            for (Product p : savedProducts) {
                CartItem item = CartItem.of(p.getProductId().getValue(), 2L);
                items.add(CartItem.of(p.getProductId().getValue(), 2L));
            }
            Cart cart = Cart.from(items);
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, savedUser.getUserId().getValue(), savedCoupon.getCouponId().getValue());

            //when
            PlaceOrderResult result = orderFacade.placeOrder(command);

            //then
            Optional<Order> order = orderRepository.findByOrderId(result.getOrderId());
            Optional<Point> pointAfterOrder = pointRepository.findByUserId(savedUser.getUserId());
            List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(result.getOrderId());
            Assertions.assertAll(
                    () -> assertThat(order.isPresent()).isTrue(),
                    () -> assertThat(pointAfterOrder.isPresent()).isTrue(),
                    () -> assertThat(pointAfterOrder.get().getBalance().getValue()).isEqualByComparingTo(BigDecimal.valueOf(976_000)),
                    () -> assertThat(orderItems.size()).isEqualTo(2)
            );
        }
    }
}
