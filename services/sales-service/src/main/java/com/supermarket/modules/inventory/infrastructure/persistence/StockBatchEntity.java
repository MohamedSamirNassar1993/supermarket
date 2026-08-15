package com.supermarket.modules.inventory.infrastructure.persistence;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class StockBatchEntity {

    private UUID id;
    private UUID organizationId;
    private UUID warehouseId;
    private UUID productId;
    private UUID variantId;
    private String batchNumber;
    private BigDecimal quantity;
    private BigDecimal remainingQuantity;
    private BigDecimal unitCost;
}
