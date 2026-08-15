package com.supermarket.modules.auth.infrastructure.persistence;

import com.supermarket.modules.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionJpaRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(String code);

    List<Permission> findAllByOrderByModuleAscActionAsc();
}
