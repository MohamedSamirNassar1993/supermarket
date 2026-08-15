package com.supermarket.modules.customers.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class InstallmentPlanResponse {

    private UUID customerId;
    private List<InstallmentResponse> installments;
}
