package com.loopers.application.like;

import com.loopers.application.like.dto.LikeCommand;
import com.loopers.application.like.dto.UnlikeCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.ConcurrentTestRunner;
import com.loopers.support.constant.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductLikeFacadeConcurrencyTest {

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductLikeRepository productLikeRepository;

    @Autowired
    ProductLikeFacade sut;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("한 명의 유저가 상품 좋아요를 동시에 여러 번 눌러도 좋아요 수는 1만 오릅니다.")
    @Test
    void like_idempotency_under_concurrency() throws Exception {
        // given
        User user = userRepository.save(User.create("gukin", "gukin@email.com", "2023-10-01", Gender.FEMALE));
        Product product = productRepository.save(new Product(10));
        LikeCommand command = LikeCommand.of(user.getLoginId(), product.getId());

        // when
        ConcurrentTestRunner.run(10, () -> {
            sut.like(command);
            return null;
        });

        // then
        Product result = productRepository.findById(product.getId()).get();
        Assertions.assertAll(
            () -> assertThat(result.getLikeCount()).isEqualTo(1)
        );
    }

    @DisplayName("한 명의 유저가 상품 좋아요 취소를 동시에 여러 번 눌러도 좋아요 수는 1만 감소합니다.")
    @Test
    void unlike_idempotency_under_concurrency() throws Exception {
        // given
        User user = userRepository.save(User.create("gukin", "gukin@email.com", "2023-10-01", Gender.FEMALE));
        Product product = productRepository.save(new Product(10));
        LikeCommand likeCommand = LikeCommand.of(user.getLoginId(), product.getId());
        sut.like(likeCommand);

        UnlikeCommand unlikeCommand = UnlikeCommand.of(user.getLoginId(), product.getId());

        // when
        ConcurrentTestRunner.run(10, () -> {
            sut.unlike(unlikeCommand);
            return null;
        });

        // then
        Product result = productRepository.findById(product.getId()).get();
        Assertions.assertAll(
                () -> assertThat(result.getLikeCount()).isZero()
        );
    }

    @DisplayName("여러 사용자가 동시에 상품 좋아요를 누르면, 좋아요 수가 사용자 수만큼 증가한다.")
    @Test
    void like_concurrently_by_multiple_users() throws Exception {
        // given
        int threadCount = 10;
        Product product = productRepository.save(new Product(100));

        // 1. 여러 사용자 생성 및 저장
        List<User> users = IntStream.range(0, threadCount)
                .mapToObj(i -> User.create("user" + i, "user" + i + "@email.com", "2025-01-01", Gender.MALE))
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        // 2. 각 사용자에 대한 '좋아요' 커맨드를 스레드-안전 큐에 추가
        ConcurrentLinkedQueue<LikeCommand> commands = new ConcurrentLinkedQueue<>();
        users.forEach(user -> commands.add(LikeCommand.of(user.getLoginId(), product.getId())));


        // when
        ConcurrentTestRunner.run(threadCount, () -> {
            LikeCommand command = commands.poll();
            if (command != null) {
                sut.like(command);
            }
            return null;
        });

        // then
        Product result = productRepository.findById(product.getId()).get();
        assertThat(result.getLikeCount()).isEqualTo(threadCount);
    }

    @DisplayName("여러 사용자가 동시에 상품 좋아요를 취소하면, 좋아요 수가 0이 된다.")
    @Test
    void unlike_concurrently_by_multiple_users() throws Exception {
        // given
        int threadCount = 10;
        Product product = productRepository.save(new Product(100));

        // 1. 여러 사용자 생성 및 저장
        List<User> users = IntStream.range(0, threadCount)
                .mapToObj(i -> User.create("user" + i, "user" + i + "@email.com", "2025-01-01", Gender.MALE))
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        // 2. 모든 사용자가 먼저 '좋아요'를 누른 상태로 만듦
        users.forEach(user -> {
            LikeCommand likeCommand = LikeCommand.of(user.getLoginId(), product.getId());
            sut.like(likeCommand);
        });
        Product initialProduct = productRepository.findById(product.getId()).get();
        assertThat(initialProduct.getLikeCount()).isEqualTo(threadCount); // 초기 상태 검증

        // 3. 각 사용자에 대한 '좋아요 취소' 커맨드를 큐에 추가
        ConcurrentLinkedQueue<UnlikeCommand> commands = new ConcurrentLinkedQueue<>();
        users.forEach(user -> commands.add(UnlikeCommand.of(user.getLoginId(), product.getId())));

        // when
        ConcurrentTestRunner.run(threadCount, () -> {
            UnlikeCommand command = commands.poll();
            if (command != null) {
                sut.unlike(command);
            }
            return null;
        });

        // then
        Product result = productRepository.findById(product.getId()).get();
        assertThat(result.getLikeCount()).isZero();
    }
}