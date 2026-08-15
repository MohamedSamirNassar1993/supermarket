package com.supermarket.modules.inventory.application.cogs;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class CogsAllocation {

    private UUID batchId;
    private String batchNumber;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
}
