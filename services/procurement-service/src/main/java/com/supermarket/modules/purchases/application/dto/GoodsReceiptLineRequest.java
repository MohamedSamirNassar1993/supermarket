package com.supermarket.modules.purchases.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class GoodsReceiptLineRequest {

    @NotNull
    private UUID orderLineId;

    @NotNull
    @Positive
    private BigDecimal receivedQuantity;

    private BigDecimal unitCost;
    private String batchNumber;
    private LocalDate expiryDate;
}
