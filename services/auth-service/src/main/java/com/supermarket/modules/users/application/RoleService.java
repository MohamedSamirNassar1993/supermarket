package com.supermarket.modules.users.application;

import com.supermarket.modules.auth.application.port.PermissionRepositoryPort;
import com.supermarket.modules.auth.application.port.RoleRepositoryPort;
import com.supermarket.modules.auth.domain.Permission;
import com.supermarket.modules.auth.domain.Role;
import com.supermarket.modules.users.api.dto.CreateRoleRequest;
import com.supermarket.modules.users.api.dto.RoleResponse;
import com.supermarket.modules.users.api.dto.UpdateRoleRequest;
import com.supermarket.modules.users.api.mapper.RoleMapper;
import com.supermarket.shared.api.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionRepositoryPort permissionRepositoryPort;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleRepositoryPort.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse findById(UUID id) {
        Role role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));
        return roleMapper.toResponse(role);
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        String roleCode = request.getCode().toUpperCase();
        if (roleRepositoryPort.existsByCode(roleCode)) {
            throw new ConflictException("Role code already exists: " + roleCode);
        }

        Role role = new Role();
        role.setCode(roleCode);
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setSystemRole(false);
        role.setActive(true);

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            role.setPermissions(new HashSet<>(permissionRepositoryPort.findAllById(request.getPermissionIds())));
        }

        return roleMapper.toResponse(roleRepositoryPort.save(role));
    }

    @Transactional
    public RoleResponse update(UUID id, UpdateRoleRequest request) {
        Role role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));

        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            role.setActive(request.getActive());
        }
        if (request.getPermissionIds() != null) {
            role.setPermissions(new HashSet<>(permissionRepositoryPort.findAllById(request.getPermissionIds())));
        }

        return roleMapper.toResponse(roleRepositoryPort.save(role));
    }

    @Transactional
    public void delete(UUID id) {
        Role role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));

        if (role.isSystemRole()) {
            throw new ConflictException("System roles cannot be deleted");
        }

        roleRepositoryPort.delete(role);
    }

    @Transactional
    public RoleResponse updatePermissions(UUID id, List<UUID> permissionIds) {
        Role role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id));

        List<Permission> permissions = permissionRepositoryPort.findAllById(new HashSet<>(permissionIds));
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toResponse(roleRepositoryPort.save(role));
    }
}
