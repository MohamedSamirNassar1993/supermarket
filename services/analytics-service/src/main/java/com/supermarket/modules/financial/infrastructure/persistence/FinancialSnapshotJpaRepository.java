package com.supermarket.modules.financial.infrastructure.persistence;

import com.supermarket.modules.financial.domain.FinancialSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialSnapshotJpaRepository extends JpaRepository<FinancialSnapshot, UUID> {

    Optional<FinancialSnapshot> findByOrganizationIdAndBranchIdAndSnapshotDateAndPeriodType(
            UUID organizationId, UUID branchId, LocalDate snapshotDate, String periodType);

    List<FinancialSnapshot> findByOrganizationIdAndSnapshotDateBetweenOrderBySnapshotDateDesc(
            UUID organizationId, LocalDate from, LocalDate to);
}
