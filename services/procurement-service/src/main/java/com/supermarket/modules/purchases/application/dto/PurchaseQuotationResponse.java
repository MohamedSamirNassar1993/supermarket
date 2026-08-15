package com.supermarket.modules.purchases.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PurchaseQuotationResponse {

    private UUID id;
    private UUID branchId;
    private UUID supplierId;
    private String quotationNumber;
    private String status;
    private LocalDate quotationDate;
    private LocalDate validUntil;
    private String currencyCode;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String notes;
    private List<PurchaseLineResponse> lines;
    private Instant createdAt;
}
