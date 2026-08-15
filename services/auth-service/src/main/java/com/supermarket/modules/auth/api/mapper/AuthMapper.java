package com.supermarket.modules.auth.api.mapper;

import com.supermarket.modules.auth.api.dto.AuthResponse;
import com.supermarket.modules.auth.domain.Permission;
import com.supermarket.modules.auth.domain.Role;
import com.supermarket.modules.auth.domain.User;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken, long expiresInMs) {
        Set<String> roleCodes = new LinkedHashSet<>();
        Set<String> permissionCodes = new LinkedHashSet<>();

        for (Role role : user.getRoles()) {
            roleCodes.add(role.getCode());
            for (Permission permission : role.getPermissions()) {
                permissionCodes.add(permission.getCode());
            }
        }

        AuthResponse.UserSummary userSummary = AuthResponse.UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(List.copyOf(roleCodes))
                .permissions(List.copyOf(permissionCodes))
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresInMs / 1000)
                .user(userSummary)
                .build();
    }
}
