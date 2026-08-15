package com.supermarket.modules.users.application;

import com.supermarket.modules.auth.application.port.PermissionRepositoryPort;
import com.supermarket.modules.users.api.dto.PermissionResponse;
import com.supermarket.modules.users.api.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepositoryPort permissionRepositoryPort;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepositoryPort.findAll().stream()
                .map(roleMapper::toPermissionResponse)
                .toList();
    }
}
