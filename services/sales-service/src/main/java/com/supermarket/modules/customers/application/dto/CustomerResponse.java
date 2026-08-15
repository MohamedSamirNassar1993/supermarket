package com.supermarket.modules.customers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CustomerResponse {

    private UUID id;
    private UUID groupId;
    private String code;
    private String name;
    private String customerType;
    private String email;
    private String phone;
    private String address;
    private String taxId;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private long loyaltyPoints;
    private boolean active;
    private String notes;
    private Instant createdAt;
}
