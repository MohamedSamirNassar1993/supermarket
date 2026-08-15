package com.supermarket.modules.purchases.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class PurchaseLineResponse {

    private UUID id;
    private UUID productId;
    private int lineNumber;
    private BigDecimal quantity;
    private BigDecimal receivedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
    private BigDecimal lineTotal;
    private String notes;
}
