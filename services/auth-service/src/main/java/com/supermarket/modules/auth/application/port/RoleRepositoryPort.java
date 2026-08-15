package com.supermarket.modules.auth.application.port;

import com.supermarket.modules.auth.domain.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepositoryPort {

    Optional<Role> findById(UUID id);

    Optional<Role> findByCode(String code);

    List<Role> findAll();

    List<Role> findAllById(Set<UUID> ids);

    Role save(Role role);

    void delete(Role role);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);
}
