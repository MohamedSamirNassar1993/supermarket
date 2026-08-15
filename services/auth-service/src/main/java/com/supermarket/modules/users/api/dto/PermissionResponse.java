package com.supermarket.modules.users.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PermissionResponse {

    private final UUID id;
    private final String code;
    private final String module;
    private final String action;
    private final String description;
}
