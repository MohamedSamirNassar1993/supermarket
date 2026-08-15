package com.supermarket.modules.suppliers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class SupplierStatementLineResponse {

    private UUID id;
    private String transactionType;
    private String referenceType;
    private UUID referenceId;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDate transactionDate;
}
