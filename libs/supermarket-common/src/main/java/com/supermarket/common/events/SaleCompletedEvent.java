package com.supermarket.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleCompletedEvent(UUID saleId, UUID branchId, BigDecimal totalAmount) {
    public static final String TYPE = "SaleCompleted";
}
