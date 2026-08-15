package com.supermarket.modules.financial.infrastructure.persistence;

import com.supermarket.modules.financial.domain.CashFlowEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CashFlowEntryJpaRepository extends JpaRepository<CashFlowEntry, UUID> {
    List<CashFlowEntry> findByOrganizationIdAndEntryDateBetween(
            UUID organizationId, LocalDate from, LocalDate to);
}
