package com.supermarket.modules.suppliers.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SupplierRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String taxId;
    private String paymentTerms;

    @NotNull
    private BigDecimal creditLimit;

    private String notes;
    private Boolean active;
}
