package com.supermarket.modules.sales.domain;

import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sales_returns")
public class SalesReturn extends AuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "return_number", nullable = false, length = 50)
    private String returnNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SaleStatus status = SaleStatus.DRAFT;

    @Column(name = "return_date", nullable = false)
    private Instant returnDate = Instant.now();

    @Column(name = "refund_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "refund_method", length = 30)
    private String refundMethod;

    @Column(name = "reason")
    private String reason;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "salesReturn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SalesReturnLine> lines = new ArrayList<>();
}
