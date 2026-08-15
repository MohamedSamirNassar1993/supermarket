package com.supermarket.modules.sales.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesPaymentRequest {

    @NotNull
    private String paymentMethod;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String reference;
}
