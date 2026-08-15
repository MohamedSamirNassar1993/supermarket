package com.supermarket.modules.purchases.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class GoodsReceiptLineResponse {

    private UUID id;
    private UUID orderLineId;
    private UUID productId;
    private BigDecimal receivedQuantity;
    private BigDecimal unitCost;
    private String batchNumber;
    private UUID stockBatchId;
}
