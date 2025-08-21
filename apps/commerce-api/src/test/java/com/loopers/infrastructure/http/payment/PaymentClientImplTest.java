package com.loopers.infrastructure.http.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.product.Money;
import com.loopers.infrastructure.http.HttpResponse;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    @MockitoBean
    PaymentFeignClient feign;

    @Autowired
    PaymentClientImpl sut;

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
                    .thenReturn(new HttpResponse<>(   // 2nd
                            new HttpResponse.Meta(HttpResponse.Meta.Result.SUCCESS, null, null),
                            null
                    ));

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
    }

}
