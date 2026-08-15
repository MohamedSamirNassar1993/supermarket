package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class SalesPaymentResponse {

    private UUID id;
    private String paymentMethod;
    private BigDecimal amount;
    private String reference;
    private Instant paidAt;
}
