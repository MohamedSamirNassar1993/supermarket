package com.supermarket.modules.warehouse.infrastructure.persistence;

import com.supermarket.modules.warehouse.domain.AdjustmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryAdjustmentRepository extends JpaRepository<InventoryAdjustmentEntity, UUID> {

    Page<InventoryAdjustmentEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<InventoryAdjustmentEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndAdjustmentNumber(UUID organizationId, String adjustmentNumber);
}
