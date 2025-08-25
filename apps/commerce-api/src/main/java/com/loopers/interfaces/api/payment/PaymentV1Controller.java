package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.dto.SyncPaymentCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.loopers.interfaces.api.ApiHeader.LOGIN_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {

    private final PaymentFacade paymentFacade;


    @Override
    @PostMapping
    public ApiResponse<PaymentV1Dto.InitiateResponse> initiatePayment(
            @RequestHeader(LOGIN_ID) String loginId,
            @RequestBody PaymentV1Dto.InitiateRequest request) {

        PayResult result = paymentFacade.initiatePayment(loginId, request.toCommand());
        return ApiResponse.success(PaymentV1Dto.InitiateResponse.from(result));
    }

    @Override
    @PostMapping("/{orderId}/callback")
    public ApiResponse<PaymentV1Dto.SyncPaymentCallbackResponse> syncPaymentCallback(
            @PathVariable(value = "orderId") Long orderId,
            @RequestBody PaymentV1Dto.SyncPaymentCallbackRequest request
    ) {
        paymentFacade.syncPaymentCallback(SyncPaymentCommand.of(orderId, request.status(), request.reason()));
        return ApiResponse.success(PaymentV1Dto.SyncPaymentCallbackResponse.builder()
                .message("결제 상태 동기화가 완료되었습니다.")
                .build());
    }
}
