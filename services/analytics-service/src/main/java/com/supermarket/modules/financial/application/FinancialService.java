package com.supermarket.modules.financial.application;

import com.supermarket.modules.financial.domain.CashFlowEntry;
import com.supermarket.modules.financial.domain.FinancialSnapshot;
import com.supermarket.modules.financial.infrastructure.persistence.CashFlowEntryJpaRepository;
import com.supermarket.modules.financial.infrastructure.persistence.FinancialSnapshotJpaRepository;
import com.supermarket.modules.financial.infrastructure.persistence.ProfitLossViewRepository;
import com.supermarket.shared.audit.AuditAction;
import com.supermarket.shared.audit.Audited;
import com.supermarket.shared.multibranch.BranchContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final FinancialSnapshotJpaRepository snapshotRepository;
    private final CashFlowEntryJpaRepository cashFlowRepository;
    private final ProfitLossViewRepository profitLossViewRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProfitAndLoss(UUID orgId, UUID branchId, LocalDate from, LocalDate to) {
        List<Object[]> rows = profitLossViewRepository.findProfitLoss(orgId, branchId, from, to);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("organizationId", row[0]);
            item.put("branchId", row[1]);
            item.put("periodStart", row[2]);
            item.put("revenue", row[3]);
            item.put("cogs", row[4]);
            item.put("grossProfit", row[5]);
            result.add(item);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> aggregateCogs(UUID orgId, UUID branchId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT COALESCE(SUM(cogs), 0) AS total_cogs
                FROM v_profit_loss
                WHERE organization_id = ?
                  AND (? IS NULL OR branch_id = ?)
                  AND period_start >= ?
                  AND period_start <= ?
                """;
        BigDecimal total = jdbcTemplate.queryForObject(
                sql, BigDecimal.class, orgId, branchId, branchId, from, to);
        return Map.of("organizationId", orgId, "branchId", branchId, "totalCogs", total != null ? total : BigDecimal.ZERO);
    }

    @Transactional
    @Audited(entityType = "CashFlowEntry", action = AuditAction.CREATE)
    public CashFlowEntry recordCashFlow(CashFlowEntry entry) {
        BranchContext.getOrganizationId().ifPresent(entry::setOrganizationId);
        BranchContext.getBranchId().ifPresent(entry::setBranchId);
        return cashFlowRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<CashFlowEntry> listCashFlow(UUID orgId, LocalDate from, LocalDate to) {
        return cashFlowRepository.findByOrganizationIdAndEntryDateBetween(orgId, from, to);
    }

    @Transactional
    @Audited(entityType = "FinancialSnapshot", action = AuditAction.CREATE)
    public FinancialSnapshot createSnapshot(FinancialSnapshot snapshot) {
        BranchContext.getOrganizationId().ifPresent(snapshot::setOrganizationId);
        BranchContext.getBranchId().ifPresent(snapshot::setBranchId);
        snapshot.setGrossProfit(snapshot.getRevenue().subtract(snapshot.getCogs()));
        snapshot.setNetProfit(snapshot.getGrossProfit().subtract(snapshot.getOperatingExpenses()));
        return snapshotRepository.save(snapshot);
    }

    @Transactional(readOnly = true)
    public List<FinancialSnapshot> listSnapshots(UUID orgId, LocalDate from, LocalDate to) {
        return snapshotRepository.findByOrganizationIdAndSnapshotDateBetweenOrderBySnapshotDateDesc(orgId, from, to);
    }
}
