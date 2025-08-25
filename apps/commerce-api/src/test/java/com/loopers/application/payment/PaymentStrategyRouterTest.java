package com.loopers.application.payment;

import com.loopers.application.payment.strategy.PaymentStrategyRouter;
import com.loopers.application.payment.strategy.PaymentStrategy;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


class PaymentStrategyRouterTest {

    private PaymentStrategyRouter sut;
    private PaymentStrategy cardStrategy;
    private PaymentStrategy pointStrategy;

    @BeforeEach
    void setUp() {
        cardStrategy = mock(PaymentStrategy.class);
        pointStrategy = mock(PaymentStrategy.class);
        // PaymentDispatcher를 다양한 전략과 함께 초기화
        sut = new PaymentStrategyRouter(List.of(cardStrategy, pointStrategy));
    }


    @DisplayName("결제 요청시")
    @Nested
    class RequestPayment {

        @DisplayName("지원하는 결제수단(CARD)이면 해당 전략을 통해 결제를 요청하고 결과를 반환한다")
        @Test
        void success_when_supported_payment_method() {
            // given
            PayCommand command = PayCommand.builder()
                    .method(PaymentMethod.CARD)
                    .build();

            PayResult expectedResult = PayResult.builder().status(PayResult.Status.SUCCESS).build();

            // cardStrategy는 CARD 결제를 지원하도록 설정
            when(cardStrategy.supports(command.getMethod())).thenReturn(true);
            // pointStrategy는 CARD 결제를 지원하지 않도록 설정
            when(pointStrategy.supports(command.getMethod())).thenReturn(false);
            // cardStrategy가 결제를 요청하면 예상된 결과를 반환하도록 설정
            when(cardStrategy.requestPayment(command)).thenReturn(expectedResult);

            // when
            PaymentStrategy paymentStrategy = sut.requestPayment(command.getMethod());
            PayResult actualResult = paymentStrategy.requestPayment(command);

            // then
            // 반환된 결과가 예상과 일치하는지 확인
            assertThat(actualResult).isEqualTo(expectedResult);
            // cardStrategy의 requestPayment가 정확히 1번 호출되었는지 확인
            verify(cardStrategy, times(1)).requestPayment(command);
            // pointStrategy의 requestPayment는 호출되지 않았는지 확인
            verify(pointStrategy, never()).requestPayment(command);
        }

        @DisplayName("지원하지 않는 결제수단이면 예외가 발생한다")
        @Test
        void fail_when_unsupported_payment_method() {
            // given
            // 임의의 결제 요청 생성
            PayCommand command = PayCommand.builder()
                    .method(PaymentMethod.POINT)
                    .build();

            // 모든 전략이 해당 결제 수단을 지원하지 않도록 설정
            when(cardStrategy.supports(command.getMethod())).thenReturn(false);
            when(pointStrategy.supports(command.getMethod())).thenReturn(false);

            // when & then
            // 예외 발생을 검증
            assertThatThrownBy(() -> sut.requestPayment(command.getMethod()).requestPayment(command))
                    .isInstanceOf(CoreException.class)
                    .hasFieldOrPropertyWithValue("errorType", ErrorType.CONFLICT)
                    .hasMessage("지원하지 않는 결제수단: " + command.getMethod());
        }
    }
}
