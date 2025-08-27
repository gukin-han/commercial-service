package com.loopers.infrastructure.data.like;

import com.loopers.common.contract.messaging.ProductLikeMessage;
import com.loopers.domain.like.ProductLikeEvent;
import com.loopers.domain.like.ProductLikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringProductLikeEventPublisher implements ProductLikeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishAdded(ProductLikeEvent.Added event) {
        ProductLikeMessage.Added message = new ProductLikeMessage.Added(
                event.productId(),
                event.eventId(),
                event.occurredAt()
        );

        eventPublisher.publishEvent(message);
    }

    @Override
    public void publishDeleted(ProductLikeEvent.Deleted event) {
        ProductLikeMessage.Deleted message = new ProductLikeMessage.Deleted(
                event.productId(),
                event.eventId(),
                event.occurredAt()
        );

        eventPublisher.publishEvent(message);
    }
}
