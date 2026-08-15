package com.supermarket.modules.purchases.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PurchaseOrderRequest {

    @NotNull
    private UUID branchId;

    @NotNull
    private UUID supplierId;

    private UUID quotationId;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private String currencyCode;
    private String notes;

    @NotEmpty
    @Valid
    private List<PurchaseLineRequest> lines;
}
