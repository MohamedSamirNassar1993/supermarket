package com.supermarket.modules.sales.application.dto;

import com.supermarket.modules.sales.domain.SaleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateSalesInvoiceRequest {

    @NotNull
    private UUID branchId;

    private UUID customerId;

    @NotNull
    private SaleType saleType;

    private String posTerminalId;
    private Long loyaltyPointsRedeemed;
    private String notes;

    @NotEmpty
    @Valid
    private List<SalesLineRequest> lines;

    @Valid
    private List<SalesPaymentRequest> payments;
}
