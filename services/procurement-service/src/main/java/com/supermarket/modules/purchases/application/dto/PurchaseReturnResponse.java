package com.supermarket.modules.purchases.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PurchaseReturnResponse {

    private UUID id;
    private UUID supplierId;
    private String returnNumber;
    private String status;
    private LocalDate returnDate;
    private BigDecimal totalAmount;
    private String reason;
    private List<PurchaseReturnLineResponse> lines;
}
