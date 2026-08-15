package com.supermarket.modules.warehouse.infrastructure.persistence;

import com.supermarket.modules.warehouse.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseTransferRepository extends JpaRepository<WarehouseTransferEntity, UUID> {

    Page<WarehouseTransferEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Page<WarehouseTransferEntity> findByOrganizationIdAndStatus(UUID organizationId, TransferStatus status, Pageable pageable);

    Optional<WarehouseTransferEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndTransferNumber(UUID organizationId, String transferNumber);
}
