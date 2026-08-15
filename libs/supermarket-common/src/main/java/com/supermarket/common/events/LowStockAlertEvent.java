package com.supermarket.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record LowStockAlertEvent(UUID productId, UUID warehouseId, BigDecimal currentQty, BigDecimal reorderLevel) {
    public static final String TYPE = "LowStockAlert";
}
