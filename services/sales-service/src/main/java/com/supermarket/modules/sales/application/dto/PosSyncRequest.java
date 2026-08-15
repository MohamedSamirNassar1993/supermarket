package com.supermarket.modules.sales.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PosSyncRequest {

    @NotNull
    private UUID branchId;

    @NotBlank
    private String terminalId;

    @NotBlank
    private String payloadType;

    @NotNull
    private Map<String, Object> payload;
}
