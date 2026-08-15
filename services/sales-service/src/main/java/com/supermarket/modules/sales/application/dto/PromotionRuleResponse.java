package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class PromotionRuleResponse {

    private UUID id;
    private String ruleType;
    private UUID productId;
    private BigDecimal minQuantity;
    private BigDecimal minAmount;
    private String discountType;
    private BigDecimal discountValue;
}
