package com.loopers.application.like;

import com.loopers.application.like.dto.LikeProductCommand;
import com.loopers.application.like.dto.UnlikeProductCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
import com.loopers.mock.jpa.FakeProductLikeRepository;
import com.loopers.mock.jpa.FakeProductRepository;
import com.loopers.mock.jpa.FakeUserRepository;
import com.loopers.support.constant.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductLikeFacadeTest {

    private ProductLikeFacade productLikeFacade;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private ProductLikeRepository productLikeRepository;

    private static final User USER = User.create("gukin", "gukin@email.com", "2023-10-01", Gender.FEMALE);

    @BeforeEach
    void setUp() {
        productRepository = new FakeProductRepository();
        userRepository = new FakeUserRepository();
        productLikeRepository = new FakeProductLikeRepository();



        productLikeFacade = new ProductLikeFacade(
                new UserService(userRepository),
                new ProductService(productRepository),
                productLikeRepository
        );
    }

    @DisplayName("상품 좋아요를 여러 번 눌러도 좋아요 수는 1만 오릅니다.")
    @Test
    void likeProduct_idempotency() {
        // given
        User user = userRepository.save(USER);
        Product product = productRepository.save(new Product(10));
        LikeProductCommand command = LikeProductCommand.of(user.getId(), product.getId());

        // when
        productLikeFacade.likeProduct(command);
        productLikeFacade.likeProduct(command);
        productLikeFacade.likeProduct(command);

        // then
        Product result = productRepository.findById(product.getId()).get();
        assertThat(result.getLikeCount()).isEqualTo(1);
    }

    @DisplayName("상품 좋아요 취소를 여러 번 눌러도 좋아요 수는 1만 감소합니다.")
    @Test
    void unlikeProduct_idempotency() {
        // given
        User user = userRepository.save(USER);
        Product product = productRepository.save(new Product(10));
        LikeProductCommand likeCommand = LikeProductCommand.of(user.getId(), product.getId());
        productLikeFacade.likeProduct(likeCommand);

        UnlikeProductCommand unlikeCommand = UnlikeProductCommand.of(user.getId(), product.getId());

        // when
        productLikeFacade.unlikeProduct(unlikeCommand);
        productLikeFacade.unlikeProduct(unlikeCommand);
        productLikeFacade.unlikeProduct(unlikeCommand);

        // then
        Product result = productRepository.findById(product.getId()).get();
        assertThat(result.getLikeCount()).isZero();
    }
}
