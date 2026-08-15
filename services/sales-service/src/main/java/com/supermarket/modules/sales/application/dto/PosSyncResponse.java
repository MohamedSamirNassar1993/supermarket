package com.supermarket.modules.sales.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PosSyncResponse {

    private UUID id;
    private String status;
    private UUID invoiceId;
}
