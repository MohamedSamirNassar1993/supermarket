package com.supermarket.modules.sales.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SalesReturnRequest {

    @NotNull
    private UUID branchId;

    private UUID invoiceId;
    private UUID customerId;
    private String refundMethod;
    private String reason;
    private String notes;

    @NotEmpty
    @Valid
    private List<SalesReturnLineRequest> lines;
}
