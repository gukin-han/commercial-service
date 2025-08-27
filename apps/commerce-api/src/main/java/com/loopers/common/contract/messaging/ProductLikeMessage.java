package com.loopers.common.contract.messaging;

import java.time.Instant;
import java.util.UUID;

public class ProductLikeMessage {
    public record Added(
            Long productId,
            UUID eventId,
            Instant occurredAt
    ) { }

    public record Deleted(
            Long productId,
            UUID eventId,
            Instant occurredAt
    ) { }


}
