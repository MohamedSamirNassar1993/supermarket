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
public class SupplierPaymentRequest {

    @NotNull
    private UUID supplierId;

    private UUID orderId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String paymentMethod;

    private LocalDate paymentDate;
    private String reference;
    private String notes;
}
