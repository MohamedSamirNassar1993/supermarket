package com.supermarket.modules.suppliers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class SupplierResponse {

    private UUID id;
    private String code;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String taxId;
    private String paymentTerms;
    private BigDecimal creditLimit;
    private BigDecimal rating;
    private boolean active;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
