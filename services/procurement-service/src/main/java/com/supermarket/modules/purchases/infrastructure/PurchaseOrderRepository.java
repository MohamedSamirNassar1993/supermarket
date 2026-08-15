package com.supermarket.modules.purchases.infrastructure;

import com.supermarket.modules.purchases.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
