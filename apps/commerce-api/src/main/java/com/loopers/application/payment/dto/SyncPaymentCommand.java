package com.loopers.application.payment.dto;

import lombok.Data;

@Data
public class SyncPaymentCommand {

    private String loginId;
    private Long orderId;

    private String status;
    private String reason;

    public static SyncPaymentCommand of(Long orderId, String status, String reason) {
        SyncPaymentCommand command = new SyncPaymentCommand();
        command.orderId = orderId;
        return command;
    }
}
