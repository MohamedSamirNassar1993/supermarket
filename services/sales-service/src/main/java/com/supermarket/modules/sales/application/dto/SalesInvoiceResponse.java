package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SalesInvoiceResponse {

    private UUID id;
    private UUID branchId;
    private UUID customerId;
    private String invoiceNumber;
    private String saleType;
    private String status;
    private Instant invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private long loyaltyPointsEarned;
    private List<SalesLineResponse> lines;
    private List<SalesPaymentResponse> payments;
}
