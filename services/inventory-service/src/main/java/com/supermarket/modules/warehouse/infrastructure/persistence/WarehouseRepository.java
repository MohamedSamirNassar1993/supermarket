package com.supermarket.modules.warehouse.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {

    Page<WarehouseEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<WarehouseEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<WarehouseEntity> findFirstByBranchIdAndActiveTrue(UUID branchId);

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
