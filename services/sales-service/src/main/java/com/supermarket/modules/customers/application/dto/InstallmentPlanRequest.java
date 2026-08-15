package com.supermarket.modules.customers.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class InstallmentPlanRequest {

    @NotNull
    private UUID referenceId;

    @NotNull
    private String referenceType;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    @NotNull
    private LocalDate firstDueDate;

    @NotNull
    @Positive
    private Integer numberOfInstallments;
}
