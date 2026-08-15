package com.supermarket.modules.users.api.mapper;

import com.supermarket.modules.auth.domain.Role;
import com.supermarket.modules.auth.domain.User;
import com.supermarket.modules.auth.domain.UserBranch;
import com.supermarket.modules.users.api.dto.RoleResponse;
import com.supermarket.modules.users.api.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class UserMapper {

    public UserResponse toResponse(User user, List<UserBranch> branches) {
        UUID primaryBranchId = branches.stream()
                .filter(UserBranch::isPrimaryBranch)
                .map(ub -> ub.getId().getBranchId())
                .findFirst()
                .orElse(null);

        List<UUID> branchIds = branches.stream()
                .map(ub -> ub.getId().getBranchId())
                .sorted()
                .toList();

        List<UserResponse.RoleSummary> roles = user.getRoles().stream()
                .sorted(Comparator.comparing(Role::getName))
                .map(role -> UserResponse.RoleSummary.builder()
                        .id(role.getId())
                        .code(role.getCode())
                        .name(role.getName())
                        .build())
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .organizationId(user.getOrganizationId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .lastLoginAt(user.getLastLoginAt())
                .roles(roles)
                .branchIds(branchIds)
                .primaryBranchId(primaryBranchId)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
