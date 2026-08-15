package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SalesLineResponse {

    private UUID id;
    private UUID productId;
    private int lineNumber;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal lineTotal;
    private UUID promotionId;
    private List<SalesLineBatchResponse> batches;
}
