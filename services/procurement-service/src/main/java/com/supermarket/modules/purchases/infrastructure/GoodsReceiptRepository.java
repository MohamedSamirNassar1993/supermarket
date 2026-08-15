package com.supermarket.modules.purchases.infrastructure;

import com.supermarket.modules.purchases.domain.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {

    Optional<GoodsReceipt> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
