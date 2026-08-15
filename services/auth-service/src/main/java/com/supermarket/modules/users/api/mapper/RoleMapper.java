package com.supermarket.modules.users.api.mapper;

import com.supermarket.modules.auth.domain.Permission;
import com.supermarket.modules.auth.domain.Role;
import com.supermarket.modules.users.api.dto.PermissionResponse;
import com.supermarket.modules.users.api.dto.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        List<RoleResponse.PermissionSummary> permissions = role.getPermissions().stream()
                .sorted(Comparator.comparing(Permission::getModule).thenComparing(Permission::getAction))
                .map(this::toPermissionSummary)
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .systemRole(role.isSystemRole())
                .active(role.isActive())
                .permissions(permissions)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .module(permission.getModule())
                .action(permission.getAction())
                .description(permission.getDescription())
                .build();
    }

    private RoleResponse.PermissionSummary toPermissionSummary(Permission permission) {
        return RoleResponse.PermissionSummary.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .module(permission.getModule())
                .action(permission.getAction())
                .description(permission.getDescription())
                .build();
    }
}
