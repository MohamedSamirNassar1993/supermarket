package com.supermarket.modules.auth.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AuthResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final long expiresIn;
    private final UserSummary user;

    @Getter
    @Builder
    public static class UserSummary {
        private final UUID id;
        private final String email;
        private final String username;
        private final String firstName;
        private final String lastName;
        private final List<String> roles;
        private final List<String> permissions;
    }
}
