package com.loopers.application.payment;

import com.loopers.application.payment.dto.InitiateCommand;
import com.loopers.application.payment.dto.SyncPaymentCommand;
import com.loopers.application.payment.strategy.PaymentStrategy;
import com.loopers.application.payment.strategy.PaymentStrategyRouter;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PayCommand;
import com.loopers.domain.payment.PayResult;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyRouter router;
    private final UserService userService;
    private final OrderService orderService;

    @Transactional
    public void syncPaymentCallback(SyncPaymentCommand command) {
        // 결제 상태 동기화 로직
        // 1. 주문 ID로 결제 정보 조회
        Payment payment = paymentRepository.findByOrderId(command.getOrderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        // 2. 결제 상태 업데이트
//        payment.syncStatus(command.getStatus());
    }

//    @Transactional
    public PayResult initiatePayment(String loginId, InitiateCommand command) {
        User user = userService.getByLoginId(loginId);
        Order order = orderService.get(command.orderId());

        PaymentStrategy paymentStrategy = router.requestPayment(command.method());

        PayCommand payCommand = PayCommand.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .loginId(user.getLoginId())
                .amount(order.getTotalPrice().subtract(order.getDiscountAmount()))
                .method(command.method())
                .callbackUrl(command.callbackUrl())
                .cardType(command.cardType())
                .cardNo(command.cardNo())
                .build();

        return paymentStrategy.requestPayment(payCommand);
    }
}
