package com.supermarket.modules.users.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private final UUID id;
    private final UUID organizationId;
    private final String email;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final boolean active;
    private final boolean emailVerified;
    private final boolean twoFactorEnabled;
    private final Instant lastLoginAt;
    private final List<RoleSummary> roles;
    private final List<UUID> branchIds;
    private final UUID primaryBranchId;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Getter
    @Builder
    public static class RoleSummary {
        private final UUID id;
        private final String code;
        private final String name;
    }
}
