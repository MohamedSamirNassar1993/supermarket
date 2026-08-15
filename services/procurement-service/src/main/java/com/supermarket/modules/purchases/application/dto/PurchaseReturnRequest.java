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
public class PurchaseReturnRequest {

    @NotNull
    private UUID branchId;

    @NotNull
    private UUID supplierId;

    private UUID orderId;
    private LocalDate returnDate;
    private String reason;
    private String notes;

    @NotEmpty
    @Valid
    private List<PurchaseReturnLineRequest> lines;
}
