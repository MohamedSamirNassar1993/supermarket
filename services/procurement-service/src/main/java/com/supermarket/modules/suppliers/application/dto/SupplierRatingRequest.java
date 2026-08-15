package com.supermarket.modules.suppliers.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SupplierRatingRequest {

    @NotNull
    @DecimalMin("0")
    @DecimalMax("5")
    private BigDecimal rating;
}
