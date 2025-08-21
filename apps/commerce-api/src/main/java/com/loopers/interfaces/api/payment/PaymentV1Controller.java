package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentAppService;
import com.loopers.application.payment.dto.SyncPaymentCommand;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.loopers.interfaces.api.ApiHeader.LOGIN_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec{

    private final PaymentAppService paymentAppService;

    @Override
    @PostMapping("/{orderId}/callback")
    public ApiResponse<PaymentV1Dto.SyncPaymentCallbackResponse> syncPaymentCallback(
            @RequestHeader(LOGIN_ID) String loginId,
            @PathVariable(value = "orderId") Long orderId,
            @RequestBody Map<String, String> requestBody
    ) {

        System.out.println(requestBody);
        paymentAppService.syncPaymentCallback(SyncPaymentCommand.of(loginId, orderId));
        return ApiResponse.success(PaymentV1Dto.SyncPaymentCallbackResponse.builder()
                .message("결제 상태 동기화가 완료되었습니다.")
                .build());
    }
}
