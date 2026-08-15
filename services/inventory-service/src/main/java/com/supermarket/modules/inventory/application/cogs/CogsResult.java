package com.supermarket.modules.inventory.application.cogs;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CogsResult {

    private BigDecimal totalQuantity;
    private BigDecimal totalCost;
    private BigDecimal averageUnitCost;
    private List<CogsAllocation> allocations;
}
