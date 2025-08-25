package com.loopers.infrastructure.http.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.product.Money;
import com.loopers.infrastructure.http.HttpResponse;
import com.loopers.infrastructure.http.payment.dto.PaymentClientV1Dto;
import com.loopers.infrastructure.http.payment.error.PgTransientException;
import feign.Request;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentClientImplTest {

    public static final HttpResponse<PaymentClientV1Dto.PaymentResponse> SUCCESS_RESPONSE = new HttpResponse<>(   // 2nd
            new HttpResponse.Meta(HttpResponse.Meta.Result.SUCCESS, null, null),
            null
    );
    @MockitoBean
    PaymentFeignClient feign;

    @Autowired
    PaymentClientImpl sut;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void resetGuards() {
        reset(feign); // Mockito 스텁도 깨끗이
        circuitBreakerRegistry.circuitBreaker("pgCircuit").reset(); // 상태+메트릭 초기화
    }

    public static final PayCommand COMMAND = new PayCommand(1L, 1L, "test", Money.ZERO, PaymentMethod.CARD, "url", "cardType", "cardNo");

    private static final Request REQUEST = Request.create(
            Request.HttpMethod.POST,
            "http://localhost:8080/api/v1/payments",
            Map.of("Content-Type", List.of("application/json")),
            new byte[]{}, // body
            StandardCharsets.UTF_8
    );

    private static final RetryableException RETRYABLE_EXCEPTION = new RetryableException(
            503, // status
            "Service Unavailable", // message
            Request.HttpMethod.POST, // httpMethod
            (Throwable) new RuntimeException("network down"), // cause
            (Long) null, // retryAfter
            REQUEST
    );

    @DisplayName("PG사 결제요청 시")
    @Nested
    class RequestPayment {

        @DisplayName("첫 시도 실패 후 다음 시도에 성공하면 성공으로 반환한다")
        @Test
        void retry_then_success() {
            when(feign.requestPayment(anyString(), any()))
                    .thenThrow(RETRYABLE_EXCEPTION)   // 1st
                    .thenReturn(SUCCESS_RESPONSE);

            PayResult result = sut.requestPayment(COMMAND);

            // then
            Assertions.assertAll(
                    () -> assertThat(result.isSuccess()).isTrue(),
                    () -> verify(feign, times(2)).requestPayment(anyString(), any())
            );
        }

        @DisplayName("2회 재시도 후에도 실패하면 fallback 메서드가 호출된다.")
        @Test
        void retry_exhausted_calls_fallback() {
            when(feign.requestPayment(anyString(), any()))
                    .thenThrow(RETRYABLE_EXCEPTION);
            PayResult result = sut.requestPayment(COMMAND);

            // then
            Assertions.assertAll(
                    () -> assertThat(result.getReason()).isEqualTo(PayResult.FailureReason.NETWORK_ERROR),
                    () -> verify(feign, times(2)).requestPayment(anyString(), any())
            );
        }

        @DisplayName("CB가 열려있을때 재시도하면 실패한다")
        @Test
        void whenCircuitIsOpen_thenRetryNotExecuted() {
            // given
            CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            cb.transitionToOpenState(); // 강제로 회로 열기

            // when
            PayResult result = sut.requestPayment(COMMAND);

            assertThat(result.isSuccess()).isFalse();
            verify(feign, never()).requestPayment(anyString(), any());  // 호출 안 되는지 확인
        }

        @DisplayName("CB가 Half-Open 상태일 때 성공 임계치를 넘으면 닫힌다")
        @Test
        void halfOpen_all_success_then_close() throws Exception {
            CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            cb.transitionToOpenState();
            Thread.sleep(250); // Half-Open 대기

            when(feign.requestPayment(anyString(), any()))
                    .thenReturn(SUCCESS_RESPONSE)
                    .thenReturn(SUCCESS_RESPONSE); // permittedNumberOfCallsInHalfOpenState=2 가정

            sut.requestPayment(COMMAND);
            sut.requestPayment(COMMAND);

            assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        }

        @DisplayName("CB기 닫힌 상태에서 실패율에 따라 Open 상태로 전환된다")
        @Test
        void closed_to_open_by_failure_rate() {
            // given
            CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            when(feign.requestPayment(anyString(), any()))
                    .thenThrow(new PgTransientException("503", "down"));

            // when
            for (int i=0; i<7; i++) {
                sut.requestPayment(COMMAND); // fallback으로 실패 처리
            }

            // then
            assertThat(cb.getState()).isIn(CircuitBreaker.State.OPEN, CircuitBreaker.State.HALF_OPEN);
        }
    }

}
