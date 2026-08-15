package com.supermarket.modules.financial.infrastructure.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProfitLossViewRepository extends Repository<Object, UUID> {

    @Query(value = """
            SELECT organization_id, branch_id, period_start, revenue, cogs, gross_profit
            FROM v_profit_loss
            WHERE organization_id = :orgId
              AND (:branchId IS NULL OR branch_id = CAST(CAST(:branchId AS TEXT) AS UUID))
              AND period_start >= :fromDate
              AND period_start <= :toDate
            ORDER BY period_start DESC
            """, nativeQuery = true)
    List<Object[]> findProfitLoss(
            @Param("orgId") UUID orgId,
            @Param("branchId") UUID branchId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
