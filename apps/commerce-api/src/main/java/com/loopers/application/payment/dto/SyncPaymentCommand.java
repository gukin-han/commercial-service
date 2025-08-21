package com.loopers.application.payment.dto;

import lombok.Data;

@Data
public class SyncPaymentCommand {

    private String loginId;
    private Long orderId;

    public static SyncPaymentCommand of(String loginId, Long orderId) {
        SyncPaymentCommand command = new SyncPaymentCommand();
        command.loginId = loginId;
        command.orderId = orderId;
        return command;
    }
}
