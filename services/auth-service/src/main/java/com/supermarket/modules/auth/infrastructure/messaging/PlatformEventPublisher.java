package com.supermarket.modules.auth.infrastructure.messaging;

import com.supermarket.common.events.DomainEvent;
import com.supermarket.shared.infrastructure.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "supermarket.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class PlatformEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PlatformEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public <T> void publish(DomainEvent<T> event) {
        rabbitTemplate.convertAndSend(RabbitConfig.PLATFORM_EVENTS_QUEUE, event);
    }
}
