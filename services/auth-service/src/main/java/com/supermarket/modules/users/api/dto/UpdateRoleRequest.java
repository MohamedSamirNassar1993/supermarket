package com.supermarket.modules.users.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateRoleRequest {

    @Size(max = 100)
    private String name;

    private String description;

    private Boolean active;

    private Set<UUID> permissionIds;
}
