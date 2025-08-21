package com.loopers.application.like;

import com.loopers.application.like.dto.LikeCommand;
import com.loopers.application.like.dto.LikeProductResult;
import com.loopers.application.like.dto.UnlikeCommand;
import com.loopers.application.like.dto.UnlikeProductResult;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.ProductId;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductLikeFacade {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional
    public LikeProductResult like(LikeCommand command) {
        User user = userService.getByLoginId(command.getLoginId());

        boolean isInserted = productLikeRepository.insertIgnoreDuplicateKey(user.getUserId(), ProductId.of(command.getProductId()));
        if (isInserted) {
            boolean isIncreased = productRepository.incrementLikeCount(command.getProductId());
            return LikeProductResult.success();
        }

        return LikeProductResult.alreadyLiked();
    }

    @Transactional
    public UnlikeProductResult unlike(UnlikeCommand command) {
        User user = userService.getByLoginId(command.getLoginId());

        boolean isDeleted = productLikeRepository.deleteByProductIdAndUserId(user.getUserId(), ProductId.of(command.getProductId()));
        if (isDeleted) {
            boolean isDecreased = productRepository.decrementLikeCount(command.getProductId());
            return UnlikeProductResult.success();
        }

        return UnlikeProductResult.alreadyUnliked();
    }
}
