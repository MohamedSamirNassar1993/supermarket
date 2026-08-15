package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.application.port.PermissionRepositoryPort;
import com.supermarket.modules.auth.domain.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public Optional<Permission> findById(UUID id) {
        return permissionJpaRepository.findById(id);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return permissionJpaRepository.findByCode(code);
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAllByOrderByModuleAscActionAsc();
    }

    @Override
    public List<Permission> findAllById(Set<UUID> ids) {
        return permissionJpaRepository.findAllById(ids);
    }
}
