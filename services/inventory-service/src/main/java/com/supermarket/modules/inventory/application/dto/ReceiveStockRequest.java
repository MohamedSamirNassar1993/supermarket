package com.supermarket.modules.inventory.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ReceiveStockRequest {

    private UUID organizationId;
    private UUID branchId;
    private UUID productId;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private String batchNumber;
    private LocalDate expiryDate;
    private String sourceType;
    private UUID sourceId;
}
