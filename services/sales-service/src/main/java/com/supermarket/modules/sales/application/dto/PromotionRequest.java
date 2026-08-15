package com.supermarket.modules.sales.application.dto;

import com.supermarket.modules.sales.domain.PromotionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class PromotionRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private PromotionType promotionType;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    private Integer priority;
    private Boolean stackable;
    private Boolean active;

    @NotEmpty
    @Valid
    private List<PromotionRuleRequest> rules;
}
