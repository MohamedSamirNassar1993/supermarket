package com.supermarket.modules.auth.application.port;

import com.supermarket.modules.auth.domain.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepositoryPort {

    Optional<Permission> findById(UUID id);

    Optional<Permission> findByCode(String code);

    List<Permission> findAll();

    List<Permission> findAllById(Set<UUID> ids);
}
