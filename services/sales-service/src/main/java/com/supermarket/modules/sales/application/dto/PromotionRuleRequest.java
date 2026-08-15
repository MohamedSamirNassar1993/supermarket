package com.supermarket.modules.sales.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PromotionRuleRequest {

    @NotBlank
    private String ruleType;

    private UUID productId;
    private BigDecimal minQuantity;
    private BigDecimal minAmount;

    @NotBlank
    private String discountType;

    @NotNull
    private BigDecimal discountValue;

    private UUID freeProductId;
    private BigDecimal freeQuantity;
}
