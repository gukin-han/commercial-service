package com.loopers.infrastructure.http.payment;

import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.PaymentClient;
import com.loopers.infrastructure.http.HttpResponse;
import com.loopers.infrastructure.http.payment.dto.PaymentClientV1Dto;
import com.loopers.infrastructure.http.payment.error.PgBusinessException;
import com.loopers.infrastructure.http.payment.error.PgTransientException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClientImpl implements PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @Retry(name = "pgRetry", fallbackMethod = "retryFallback")
    @Override
    public PayResult requestPayment(PayCommand command) {
        HttpResponse<PaymentClientV1Dto.PaymentResponse> response = paymentFeignClient.requestPayment(
                command.getLoginId(),
                PaymentClientV1Dto.PaymentRequest.from(command)
        );

        HttpResponse.Meta meta = response.meta();
        if (meta.result() == HttpResponse.Meta.Result.SUCCESS) {
            return response.toSuccessPayResult();
        }

        // 바디가 FAIL인 경우 분기: 일시적이면 재시도, 영구적이면 즉시 실패
        if (isTransient(meta.result())) {
            throw new PgTransientException(meta.errorCode(), meta.message());
        } else {
            throw new PgBusinessException(meta.errorCode(), meta.message());
        }
    }

    private boolean isTransient(HttpResponse.Meta.Result result) {
        return HttpResponse.Meta.Result.FAIL.equals(result);
    }

    @SuppressWarnings("unused")
    private PayResult retryFallback(PayCommand command, Throwable t) {
        log.warn("PG 요청 재시도 실패: {}, command: {}", t.getMessage(), command);
        return PayResult.fail(
                (t instanceof PgBusinessException)
                        ? PayResult.FailureReason.UPSTREAM_FAILURE    // 재시도 불가 성격
                        : PayResult.FailureReason.NETWORK_ERROR       // 재시도 소진 or 네트워크류
        );
    }
}
