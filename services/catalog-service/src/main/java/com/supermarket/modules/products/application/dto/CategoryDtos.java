package com.supermarket.modules.products.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class CategoryDtos {

    private CategoryDtos() {
    }

    @Getter
    @Setter
    public static class CreateRequest {
        @NotNull
        private UUID organizationId;
        private UUID parentId;
        @NotBlank @Size(max = 50)
        private String code;
        @NotBlank @Size(max = 255)
        private String name;
        private String description;
        @DecimalMin("0")
        private BigDecimal markupPercent;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        private UUID parentId;
        @Size(max = 255)
        private String name;
        private String description;
        @DecimalMin("0")
        private BigDecimal markupPercent;
        private Boolean active;
    }

    @Getter
    @Builder
    public static class Response {
        private UUID id;
        private UUID organizationId;
        private UUID parentId;
        private String code;
        private String name;
        private String description;
        private BigDecimal markupPercent;
        private boolean active;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
