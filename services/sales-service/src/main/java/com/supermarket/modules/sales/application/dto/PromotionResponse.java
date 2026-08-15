package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PromotionResponse {

    private UUID id;
    private String code;
    private String name;
    private String promotionType;
    private Instant startDate;
    private Instant endDate;
    private int priority;
    private boolean stackable;
    private boolean active;
    private List<PromotionRuleResponse> rules;
}
