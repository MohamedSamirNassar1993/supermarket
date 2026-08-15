package com.supermarket.common.events;

import java.time.Instant;
import java.util.UUID;

public record DomainEvent<T>(
        String eventId,
        String eventType,
        Instant occurredAt,
        String source,
        T payload
) {
    public static <T> DomainEvent<T> of(String eventType, String source, T payload) {
        return new DomainEvent<>(UUID.randomUUID().toString(), eventType, Instant.now(), source, payload);
    }
}
