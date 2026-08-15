package com.supermarket.modules.notifications.infrastructure.messaging;

import com.supermarket.modules.notifications.application.NotificationService;
import com.supermarket.shared.infrastructure.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "supermarket.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class PlatformEventNotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitConfig.PLATFORM_EVENTS_QUEUE)
    public void handlePlatformEvent(Map<String, Object> event) {
        log.info("Processing platform event for notification: {}", event.get("eventType"));
        String eventType = resolveEventType(event);
        UUID userId = resolveUserId(event);
        if (userId == null) {
            return;
        }
        notificationService.notifyUser(userId, eventType,
                resolveTitle(eventType),
                resolveMessage(eventType, event),
                "INFO");
    }

    private String resolveEventType(Map<String, Object> event) {
        Object eventType = event.get("eventType");
        if (eventType != null) {
            return String.valueOf(eventType);
        }
        return String.valueOf(event.getOrDefault("type", "GENERIC"));
    }

    private UUID resolveUserId(Map<String, Object> event) {
        UUID direct = parseUserId(event.get("userId"));
        if (direct != null) {
            return direct;
        }
        Object payload = event.get("payload");
        if (payload instanceof Map<?, ?> payloadMap) {
            return parseUserId(payloadMap.get("userId"));
        }
        return null;
    }

    private String resolveTitle(String eventType) {
        return switch (eventType) {
            case "UserCreated" -> "Welcome";
            case "LoginSuccess" -> "Login successful";
            default -> "System Notification";
        };
    }

    @SuppressWarnings("unchecked")
    private String resolveMessage(String eventType, Map<String, Object> event) {
        Object payload = event.get("payload");
        if (payload instanceof Map<?, ?> payloadMap) {
            Map<String, Object> map = (Map<String, Object>) payloadMap;
            if ("UserCreated".equals(eventType)) {
                return "Account created for " + map.getOrDefault("email", "user");
            }
            if ("LoginSuccess".equals(eventType)) {
                return "Successful login for " + map.getOrDefault("email", "user");
            }
        }
        return String.valueOf(event.getOrDefault("message", ""));
    }

    private UUID parseUserId(Object value) {
        if (value instanceof UUID uuid) {
            return uuid;
        }
        if (value instanceof String s && !s.isBlank()) {
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }
}
