package com.supermarket.modules.products.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

public final class BrandDtos {

    private BrandDtos() {
    }

    @Getter
    @Setter
    public static class CreateRequest {
        @NotNull
        private UUID organizationId;
        @NotBlank @Size(max = 50)
        private String code;
        @NotBlank @Size(max = 255)
        private String name;
        private String description;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        @Size(max = 255)
        private String name;
        private String description;
        private Boolean active;
    }

    @Getter
    @Builder
    public static class Response {
        private UUID id;
        private UUID organizationId;
        private String code;
        private String name;
        private String description;
        private boolean active;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
