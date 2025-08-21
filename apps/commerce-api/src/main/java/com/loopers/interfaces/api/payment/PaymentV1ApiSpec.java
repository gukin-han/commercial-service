package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Payment V1 API", description = "결제 관리 API")
public interface PaymentV1ApiSpec {

    ApiResponse<PaymentV1Dto.InitiateResponse> initiatePayment(
            @Schema(description = "X-USER-ID")
            String loginId,
            @Schema(description = "결제 요청 정보")
            PaymentV1Dto.InitiateRequest request
    );

    @Operation(
            summary = "결제 동기화 콜백",
            description = "결제 시스템에서 결제 상태를 동기화하기 위한 콜백 API"
    )
    ApiResponse<PaymentV1Dto.SyncPaymentCallbackResponse> syncPaymentCallback(
            @Schema(description = "X-USER-ID")
            String loginId,
            @Schema(description = "동기화 대상 주문 ID")
            Long orderId,
            Map<String, String> requestBody

    );
}
