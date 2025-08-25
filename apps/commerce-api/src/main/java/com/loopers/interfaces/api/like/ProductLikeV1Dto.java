package com.loopers.interfaces.api.like;

import com.loopers.application.like.dto.LikeProductResult;
import com.loopers.application.like.dto.UnlikeProductResult;

public class ProductLikeV1Dto {

    public static class LikeResponse {
        public static LikeResponse fromResult(LikeProductResult result) {
            return null;
        }
    }

    public static class UnlikeResponse {
        public static UnlikeResponse fromResult(UnlikeProductResult result) {
            return null;
        }
    }
}
