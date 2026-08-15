package com.supermarket.modules.customers.application.dto;

import com.supermarket.modules.customers.domain.CustomerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CustomerRequest {

    private UUID groupId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private CustomerType customerType;

    private String email;
    private String phone;
    private String address;
    private String taxId;

    @NotNull
    private BigDecimal creditLimit;

    private String notes;
    private Boolean active;
}
