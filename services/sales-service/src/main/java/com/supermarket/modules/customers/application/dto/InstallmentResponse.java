package com.supermarket.modules.customers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class InstallmentResponse {

    private UUID id;
    private int installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String status;
}
