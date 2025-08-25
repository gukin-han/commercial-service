package com.loopers.application.like;

import com.loopers.application.like.dto.LikeProductCommand;
import com.loopers.application.like.dto.LikeProductResult;
import com.loopers.application.like.dto.UnlikeProductCommand;
import com.loopers.application.like.dto.UnlikeProductResult;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserId;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductLikeFacade {

    private final UserService userService;
    private final ProductService productService;
    private final ProductLikeRepository productLikeRepository;

    @Transactional
    public LikeProductResult likeProduct(LikeProductCommand command) {
        User user = userService.findByUserId(UserId.of(command.getUserId()));
        Product product = productService.findByProductId(ProductId.of(command.getProductId()));

        if (productLikeRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            return LikeProductResult.alreadyLiked();
        }

        ProductLike productLike = ProductLike.create(user.getUserId(), product.getProductId());
        productLikeRepository.save(productLike);
        product.increaseLikeCount();

        return LikeProductResult.success();
    }

    @Transactional
    public UnlikeProductResult unlikeProduct(UnlikeProductCommand command) {
        User user = userService.findByUserId(UserId.of(command.getUserId()));
        Product product = productService.findByProductId(ProductId.of(command.getProductId()));


        return productLikeRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .map(productLike -> {
                    productLikeRepository.delete(productLike);
                    product.decreaseLikeCount();
                    return UnlikeProductResult.success();
                })
                .orElseGet(UnlikeProductResult::alreadyUnliked);
    }
}
