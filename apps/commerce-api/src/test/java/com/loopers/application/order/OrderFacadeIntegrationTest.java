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
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Money;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.Stock;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.common.constant.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        private User user;
        private Point point;
        private Coupon coupon;
        private Product product1;
        private Product product2;
        private List<Product> savedProducts;

        @BeforeEach
        void setUp() {
            user = User.create("gukin", "gukin@email.com", "2025-10-10", Gender.FEMALE);
            user = userRepository.save(user);

            point = Point.create(user, Money.of(1_000_000));
            point = pointRepository.save(point);

            coupon = Coupon.create(CouponType.PERCENTAGE, user.getUserId(), null, Percent.of(0.2));
            coupon = couponRepository.save(coupon);

            product1 = Product.create(Stock.of(10), "맥북", Money.of(10_000), BrandId.of(1L));
            product2 = Product.create(Stock.of(10), "아이폰", Money.of(5_000), BrandId.of(1L));
            savedProducts = productRepository.saveAll(List.of(product1, product2));
        }

        @DisplayName("정상 처리된다")
        @Test
        void shouldPlaceOrderSuccessfully() {
            //given
            List<CartItem> items = new ArrayList<>();
            for (Product p : savedProducts) {
                CartItem item = CartItem.of(p.getProductId().getValue(), 2L);
                items.add(CartItem.of(p.getProductId().getValue(), 2L));
            }
            Cart cart = Cart.from(items);
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, user.getUserId().getValue(), coupon.getCouponId().getValue(), PaymentMethod.POINT);

            //when
            PlaceOrderResult result = orderFacade.placeOrder(command);

            //then
            Optional<Order> order = orderRepository.findByOrderId(result.getOrderId());
            Optional<Point> pointAfterOrder = pointRepository.findByUserId(user.getUserId());
            List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(result.getOrderId());
            Assertions.assertAll(
                    () -> assertThat(order.isPresent()).isTrue(),
                    () -> assertThat(pointAfterOrder.isPresent()).isTrue(),
                    () -> assertThat(pointAfterOrder.get().getBalance().getValue()).isEqualByComparingTo(BigDecimal.valueOf(976_000)),
                    () -> assertThat(orderItems.size()).isEqualTo(2)
            );
        }

        @DisplayName("쿠폰 없이도 정상 처리된다")
        @Test
        void shouldPlaceOrderSuccessfully_whenNoCouponIsProvided() {
            //given
            List<CartItem> items = savedProducts.stream()
                    .map(p -> CartItem.of(p.getProductId().getValue(), 2L))
                    .toList();
            Cart cart = Cart.from(items);
            // command에 couponId로 null을 전달
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, user.getUserId().getValue(), null, PaymentMethod.POINT);

            //when
            PlaceOrderResult result = orderFacade.placeOrder(command);

            //then
            Optional<Order> order = orderRepository.findByOrderId(result.getOrderId());
            Optional<Point> pointAfterOrder = pointRepository.findByUserId(user.getUserId());
            List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(result.getOrderId());

            // 총 상품 금액: (10,000 * 2) + (5,000 * 2) = 30,000
            // 초기 포인트: 1,000,000
            // 남은 포인트: 1,000,000 - 30,000 = 970,000
            Assertions.assertAll(
                () -> assertThat(order.isPresent()).isTrue(),
                () -> assertThat(pointAfterOrder.isPresent()).isTrue(),
                () -> assertThat(pointAfterOrder.get().getBalance().getValue()).isEqualByComparingTo(BigDecimal.valueOf(970_000)),
                () -> assertThat(orderItems.size()).isEqualTo(2)
            );
        }

        @DisplayName("동시 주문 시 사용된 쿠폰을 재사용시 예외가 발생한다")
        @Test
        void concurrent() throws Exception {
            // given
            int threadCount = 10;
            ExecutorService pool = Executors.newFixedThreadPool(threadCount);

            // 주문할 상품 준비
            List<CartItem> items = savedProducts.stream()
                    .map(p -> CartItem.of(p.getProductId().getValue(), 1L))
                    .toList();
            Cart cart = Cart.from(items);
            PlaceOrderCommand command = PlaceOrderCommand.of(cart, user.getUserId().getValue(), coupon.getCouponId().getValue(), PaymentMethod.POINT);

            // 동시 시작/종료 장치
            CyclicBarrier start = new CyclicBarrier(threadCount);
            CountDownLatch done = new CountDownLatch(threadCount);
            // 결과 수집 (스레드 안전)
            List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
            List<PlaceOrderResult> successes = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < threadCount; i++) {
                pool.submit(() -> {
                    try {
                        start.await(); // 모두 모여서 동시에 시작
                        PlaceOrderResult r = orderFacade.placeOrder(command);
                        successes.add(r);
                    } catch (Throwable t) {
                        errors.add(t);
                    } finally {
                        done.countDown();
                    }
                });
            }

            done.await();
            pool.shutdown();

            Assertions.assertAll(
                    () -> assertThat(successes).hasSize(1),
                    () -> assertThat(errors).hasSize(9)
            );
        }
    }
}
