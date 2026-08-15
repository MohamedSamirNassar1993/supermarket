package com.supermarket.modules.financial.domain;

import com.supermarket.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "financial_snapshots")
@Getter
@Setter
public class FinancialSnapshot extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "period_type", nullable = false, length = 20)
    private String periodType = "DAILY";

    @Column(name = "revenue", nullable = false, precision = 19, scale = 4)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "cogs", nullable = false, precision = 19, scale = 4)
    private BigDecimal cogs = BigDecimal.ZERO;

    @Column(name = "gross_profit", nullable = false, precision = 19, scale = 4)
    private BigDecimal grossProfit = BigDecimal.ZERO;

    @Column(name = "operating_expenses", nullable = false, precision = 19, scale = 4)
    private BigDecimal operatingExpenses = BigDecimal.ZERO;

    @Column(name = "net_profit", nullable = false, precision = 19, scale = 4)
    private BigDecimal netProfit = BigDecimal.ZERO;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode = "USD";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
