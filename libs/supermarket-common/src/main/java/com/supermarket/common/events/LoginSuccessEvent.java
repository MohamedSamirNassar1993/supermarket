package com.supermarket.common.events;

import java.util.UUID;

public record LoginSuccessEvent(UUID userId, String email) {
    public static final String TYPE = "LoginSuccess";
}
