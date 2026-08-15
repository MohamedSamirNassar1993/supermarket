package com.supermarket.common.events;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, String email, String fullName) {
    public static final String TYPE = "UserCreated";
}
