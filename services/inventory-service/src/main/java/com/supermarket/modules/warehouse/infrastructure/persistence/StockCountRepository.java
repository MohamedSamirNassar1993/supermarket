package com.supermarket.modules.warehouse.infrastructure.persistence;

import com.supermarket.modules.warehouse.domain.StockCountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockCountRepository extends JpaRepository<StockCountEntity, UUID> {

    Page<StockCountEntity> findByOrganizationId(UUID organizationId, Pageable pageable);

    Optional<StockCountEntity> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndCountNumber(UUID organizationId, String countNumber);
}
