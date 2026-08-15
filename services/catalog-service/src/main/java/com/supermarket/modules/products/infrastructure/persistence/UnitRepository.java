package com.supermarket.modules.products.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<UnitEntity, UUID> {

    Page<UnitEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<UnitEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
