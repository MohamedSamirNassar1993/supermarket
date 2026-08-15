package com.supermarket.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record StockReceivedEvent(
        UUID organizationId,
        UUID branchId,
        UUID purchaseId,
        UUID warehouseId,
        UUID productId,
        BigDecimal quantity,
        BigDecimal unitCost
) {
    public static final String TYPE = "StockReceived";
}
