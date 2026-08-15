package com.supermarket.modules.customers.domain;

import com.supermarket.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "loyalty_transactions")
public class LoyaltyTransaction extends AuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType;

    @Column(name = "points", nullable = false)
    private long points;

    @Column(name = "balance_after", nullable = false)
    private long balanceAfter;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "description")
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private java.time.LocalDate transactionDate = java.time.LocalDate.now();
}
