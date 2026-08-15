package com.supermarket.modules.customers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class CustomerGroupResponse {

    private UUID id;
    private String code;
    private String name;
    private BigDecimal discountRate;
    private String description;
    private boolean active;
}
