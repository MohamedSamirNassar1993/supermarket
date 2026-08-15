package com.supermarket.modules.suppliers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class SupplierBalanceResponse {

    private UUID supplierId;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
}
