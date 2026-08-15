package com.supermarket.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record StockAllocatedEvent(
        UUID saleId,
        UUID warehouseId,
        UUID productId,
        BigDecimal quantity
) {
    public static final String TYPE = "StockAllocated";
}
