package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class SalesLineBatchResponse {

    private UUID stockBatchId;
    private BigDecimal quantity;
}
