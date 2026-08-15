package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class SalesReturnResponse {

    private UUID id;
    private UUID invoiceId;
    private String returnNumber;
    private String status;
    private BigDecimal refundAmount;
    private String refundMethod;
}
