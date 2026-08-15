package com.supermarket.modules.inventory.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovementEntity, UUID> {

    Page<InventoryMovementEntity> findByOrganizationIdOrderByOccurredAtDesc(UUID organizationId, Pageable pageable);

    Page<InventoryMovementEntity> findByWarehouseIdOrderByOccurredAtDesc(UUID warehouseId, Pageable pageable);
}
