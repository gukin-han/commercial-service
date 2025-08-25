package com.loopers.infrastructure.http.payment;

import com.loopers.infrastructure.http.HttpResponse;
import com.loopers.infrastructure.http.payment.dto.PaymentClientV1Dto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "PaymentFeignClient",
        url = "http://localhost:8082",
        configuration = PaymentFeignClientConfig.class
)
@Headers("Content-Type: application/json")
public interface PaymentFeignClient {

    @PostMapping("/api/v1/payment")
    HttpResponse<PaymentClientV1Dto.PaymentResponse> requestPayment(
            @RequestHeader("X-USER-ID") String loginId,
            @RequestBody PaymentClientV1Dto.PaymentRequest request
    );

    @GetMapping("/api/v1/payment}")
    HttpResponse<PaymentClientV1Dto.getResponse> getTransactionsByOrderId(
            @RequestParam Long orderId
    );
}
