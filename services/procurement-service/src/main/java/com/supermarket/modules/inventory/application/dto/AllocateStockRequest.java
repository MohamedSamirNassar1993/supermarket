package com.supermarket.modules.inventory.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class AllocateStockRequest {

    private UUID organizationId;
    private UUID branchId;
    private UUID productId;
    private BigDecimal quantity;
    private String referenceType;
    private UUID referenceId;
}
