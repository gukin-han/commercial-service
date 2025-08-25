package com.loopers.domain.product;

import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @DisplayName("재고 차감시")
    @Nested
    class DecreaseStock {

        @DisplayName("품절 상태인 경우 409 Conflict 예외를 던진다")
        @Test
        void throwsConflictException_whenStoppedOrSoldOutProduct(){
            long stockQuantity = 9;
            Product product = new Product(stockQuantity);
            product.decreaseStock(stockQuantity);
            long decreasingQuantity = 1;

            //when
            CoreException result = assertThrows(CoreException.class, () -> product.decreaseStock(decreasingQuantity));

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }

        @DisplayName("남은 재고가 0보다 작은 경우 409 Conflict 예외를 던진다")
        @Test
        void throwsConflictException_whenRemainingStockLessThanZero() {
            //given
            long stockQuantity = 9;
            Product product = new Product(stockQuantity);
            long decreasingQuantity = 10;

            //when
            CoreException result = assertThrows(CoreException.class, () -> product.decreaseStock(decreasingQuantity));

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }

        @DisplayName("남은 재고가 0 보다 크거나 같은 경우 정상 처리된다")
        @ParameterizedTest
        @MethodSource("provideValidDecreasingQuantity")
        void decreaseStockSuccessfully_whenRemainingStockGreaterThanAndEqualZero(
                long stockQuantity,
                long decreasingQuantity,
                long expectedQuantity,
                ProductStatus expectedStatus
        ) {
            //given
            Product product = new Product(stockQuantity);

            //when
            product.decreaseStock(decreasingQuantity);

            //then
            Assertions.assertAll(
                    () -> assertThat(product.getStock().getQuantity()).isEqualTo(expectedQuantity),
                    () -> assertThat(product.getStatus()).isEqualTo(expectedStatus)
            );
        }

        static Stream<Arguments> provideValidDecreasingQuantity() {
            return Stream.of(
                    Arguments.of(10, 10, 0, ProductStatus.SOLD_OUT),
                    Arguments.of(10, 9, 1, ProductStatus.ACTIVE)
            );
        }
    }

    @DisplayName("상품의 좋아요 수를 증가시")
    @Nested
    class IncreaseLikeCount {

        @DisplayName("정상적으로 처리된다")
        @Test
        void increaseLikeCountSuccessfully() {
            //given
            Product product = new Product(10L);

            //when
            product.increaseLikeCount();

            //then
            assertThat(product.getLikeCount()).isEqualTo(1);
        }
    }

    @DisplayName("상품의 좋아요 수를 감소시")
    @Nested
    class DecreaseLikeCount {

        @DisplayName("좋아요 수가 0보다 작으면 409 Conflict 예외를 던진다")
        @Test
        void throwConflictException_whenLikeCountLessThanZero() {
            //given
            Product product = new Product(10L);

            //when
            CoreException result = assertThrows(CoreException.class, product::decreaseLikeCount);

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.CONFLICT);
        }

        @DisplayName("좋아요 수가 0보다 크면 정상 처리된다")
        @Test
        void decreaseCountLikeSuccessfully() {
            //given
            Product product = new Product(10L);
            product.increaseLikeCount();

            //when
            product.decreaseLikeCount();

            //then
            assertThat(product.getLikeCount()).isEqualTo(0);
        }
    }
}
