package com.supermarket.modules.sales.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SalesLineRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private BigDecimal quantity;

    private BigDecimal unitPrice;
    private BigDecimal taxRate;
}
