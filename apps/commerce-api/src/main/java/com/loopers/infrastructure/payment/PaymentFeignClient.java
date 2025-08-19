package com.loopers.infrastructure.payment;

import com.loopers.infrastructure.payment.dto.PaymentRequest;
import com.loopers.infrastructure.payment.dto.PaymentResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "PaymentFeignClient",
        url = "http://localhost:8082"
)
@Headers("Content-Type: application/json")
public interface PaymentFeignClient {

    @PostMapping("/api/v1/payments")
    PaymentResponse requestPayment(
            @RequestHeader("X-USER-ID") String loginId,
            @RequestBody PaymentRequest request
    );

}
