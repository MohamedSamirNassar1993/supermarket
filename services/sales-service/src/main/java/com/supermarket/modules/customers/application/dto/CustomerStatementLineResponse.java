package com.supermarket.modules.customers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CustomerStatementLineResponse {

    private UUID id;
    private String transactionType;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDate transactionDate;
}
