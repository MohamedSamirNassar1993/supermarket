package com.supermarket.modules.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.common.events.DomainEvent;
import com.supermarket.common.events.LoginSuccessEvent;
import com.supermarket.common.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${supermarket.rabbitmq.platform-events-queue:platform.events}")
    private String eventsQueue;

    public void publishUserCreated(UUID userId, String email, String fullName) {
        publish(UserCreatedEvent.TYPE, new UserCreatedEvent(userId, email, fullName));
    }

    public void publishLoginSuccess(UUID userId, String email) {
        publish(LoginSuccessEvent.TYPE, new LoginSuccessEvent(userId, email));
    }

    private void publish(String eventType, Object payload) {
        DomainEvent<Object> event = DomainEvent.of(eventType, "auth-service", payload);
        rabbitTemplate.convertAndSend(eventsQueue, event);
    }
}
