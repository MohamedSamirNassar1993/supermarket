package com.supermarket.modules.sales.infrastructure;

import com.supermarket.modules.sales.domain.SalesInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, UUID> {

    Optional<SalesInvoice> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
