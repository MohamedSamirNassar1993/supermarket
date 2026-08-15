package com.supermarket.modules.purchases.infrastructure;

import com.supermarket.modules.purchases.domain.PurchaseQuotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseQuotationRepository extends JpaRepository<PurchaseQuotation, UUID> {

    Optional<PurchaseQuotation> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
