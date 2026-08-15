package com.supermarket.modules.inventory.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class BatchAllocation {

    private UUID batchId;
    private BigDecimal quantity;
}
