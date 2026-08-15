package com.supermarket.modules.purchases.infrastructure;

import com.supermarket.modules.purchases.domain.PurchaseReturn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseReturnRepository extends JpaRepository<PurchaseReturn, UUID> {

    Optional<PurchaseReturn> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
