package com.supermarket.modules.users.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RoleResponse {

    private final UUID id;
    private final String code;
    private final String name;
    private final String description;
    private final boolean systemRole;
    private final boolean active;
    private final List<PermissionSummary> permissions;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Getter
    @Builder
    public static class PermissionSummary {
        private final UUID id;
        private final String code;
        private final String module;
        private final String action;
        private final String description;
    }
}
