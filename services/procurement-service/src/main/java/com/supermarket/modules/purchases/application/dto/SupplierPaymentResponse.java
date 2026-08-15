package com.supermarket.modules.purchases.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class SupplierPaymentResponse {

    private UUID id;
    private UUID supplierId;
    private UUID orderId;
    private String paymentNumber;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
}
