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
import com.loopers.support.ConcurrentTestResult;
import com.loopers.support.ConcurrentTestRunner;
import com.loopers.support.constant.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderFacadeConcurrencyTest {

    private final DatabaseCleanUp databaseCleanUp;
    private final OrderFacade orderFacade;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PointRepository pointRepository;
    private final CouponRepository couponRepository;

    @Autowired
    OrderFacadeConcurrencyTest(DatabaseCleanUp databaseCleanUp, OrderFacade orderFacade, UserRepository userRepository, ProductRepository productRepository, PointRepository pointRepository, CouponRepository couponRepository) {
        this.databaseCleanUp = databaseCleanUp;
        this.orderFacade = orderFacade;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.pointRepository = pointRepository;
        this.couponRepository = couponRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("동시 주문 요청시")
    @Nested
    class PlaceOrderConcurrently {

        private User user1;
        private User user2;
        private Point point1;
        private Point point2;
        private Coupon coupon;
        private Product product1;
        private Product product2;
        private List<Product> savedProducts;

        @BeforeEach
        void setUp() {
            user1 = User.create("gukin1", "test1@email.com", "2025-10-10", Gender.FEMALE);
            user2 = User.create("gukin2", "test2@email.com", "2025-10-10", Gender.MALE);
            user1 = userRepository.save(user1);
            user2 = userRepository.save(user2);

            point1 = Point.create(user1, Money.of(1_000_000));
            point2 = Point.create(user2, Money.of(1_000_000));
            point1 = pointRepository.save(point1);
            point2 = pointRepository.save(point2);

            coupon = Coupon.create(CouponType.PERCENTAGE, user1.getUserId(), null, Percent.of(0.2));
            coupon = couponRepository.save(coupon);

            product1 = Product.create(Stock.of(1000), "맥북", Money.of(10_000), BrandId.of(1L));
            product2 = Product.create(Stock.of(1000), "아이폰", Money.of(5_000), BrandId.of(1L));
            savedProducts = productRepository.saveAll(List.of(product1, product2));
        }


        @DisplayName("동일한 쿠폰으로 여러 기기에서 동시에 주문해도, 쿠폰은 단 한번만 사용되어야 한다")
        @Test
        void useCouponExactlyOnce() throws Exception {
            // given
            List<CartItem> items = savedProducts.stream()
                    .map(p -> CartItem.of(p.getProductId().getValue(), 1L))
                    .toList();
            Cart cart = Cart.from(items);
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, user1.getUserId().getValue(), coupon.getCouponId().getValue());

            // when
            ConcurrentTestResult<PlaceOrderResult> result = ConcurrentTestRunner.run(
                    10,
                    () -> orderFacade.placeOrder(command)
            );

            // then
            assertThat(result.getSuccesses()).hasSize(1);
            assertThat(result.getErrors()).hasSize(9);
            assertThat(result.getErrors().get(0)).isInstanceOf(ObjectOptimisticLockingFailureException.class);
        }

        @DisplayName("동일한 유저가 서로 다른 주문을 동시에 수행해도, 포인트가 정상적으로 차감되어야 한다.")
        @Test
        void deductPointsProperly() throws Exception {
            // given
            List<CartItem> items = savedProducts.stream()
                    .map(p -> CartItem.of(p.getProductId().getValue(), 1L))
                    .toList();
            Cart cart = Cart.from(items);
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, user1.getUserId().getValue(), null);

            // when
            ConcurrentTestResult<PlaceOrderResult> result = ConcurrentTestRunner.run(
                    10,
                    () -> orderFacade.placeOrder(command)
            );

            // then
            Point point = pointRepository.findByUserId(user1.getUserId()).get();
            Assertions.assertAll(
                    () -> assertThat(result.getSuccesses()).hasSize(10),
                    () -> assertThat(result.getErrors()).hasSize(0),
                    () -> assertThat(point.getBalance().getValue()).isEqualByComparingTo(BigDecimal.valueOf(850_000))
            );
        }

        @DisplayName("동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다")
        @Test
        void deductStocksProperly() throws Exception {
            // given
            List<CartItem> items = savedProducts.stream()
                    .map(p -> CartItem.of(p.getProductId().getValue(), 1L))
                    .toList();
            Cart cart = Cart.from(items);
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, user1.getUserId().getValue(), null);

            // when
            ConcurrentTestResult<PlaceOrderResult> result = ConcurrentTestRunner.run(
                    10,
                    () -> orderFacade.placeOrder(command)
            );

            // then
            Product product1 = productRepository.findById(savedProducts.get(0).getProductId().getValue()).get();
            Product product2 = productRepository.findById(savedProducts.get(1).getProductId().getValue()).get();
            Assertions.assertAll(
                    () -> assertThat(result.getSuccesses()).hasSize(10),
                    () -> assertThat(result.getErrors()).hasSize(0),
                    () -> assertThat(product1.getStock().getQuantity()).isEqualTo(990),
                    () -> assertThat(product2.getStock().getQuantity()).isEqualTo(990)
            );
        }
    }
}
